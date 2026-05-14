package net.grim.mysticmachine.block.entity;

import net.grim.mysticmachine.block.custom.LiquidPipeBlock;
import net.grim.mysticmachine.block.entity.renderer.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;

import java.util.ArrayList;
import java.util.List;

public class LiquidPipeBlockEntity extends BlockEntity {

    private static final int CAPACITY = 4000;
    private static final int TRANSFER_RATE = 1000;   // mB per tick per connection

    public final FluidTank tank = new FluidTank(CAPACITY) {
        @Override
        protected void onContentsChanged() {
            setChanged();
        }
    };

    public LiquidPipeBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.LIQUID_PIPE_BE.get(), pos, state);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, LiquidPipeBlockEntity pipe) {
        if (level.isClientSide) return;

        List<Direction> connectedDirs = getConnectedDirections(state);
        if (connectedDirs.isEmpty()) return;

        // === PULL PHASE ===
        for (Direction dir : connectedDirs) {
            // TODO: Later replace with real side config (Pull / Normal)
            IFluidHandler source = level.getCapability(
                    Capabilities.FluidHandler.BLOCK,
                    pos.relative(dir),
                    dir.getOpposite()
            );

            if (source == null) continue;

            FluidStack drained = source.drain(TRANSFER_RATE, IFluidHandler.FluidAction.SIMULATE);
            if (drained.isEmpty()) continue;

            int filled = pipe.tank.fill(drained, IFluidHandler.FluidAction.EXECUTE);
            if (filled > 0) {
                source.drain(filled, IFluidHandler.FluidAction.EXECUTE);
            }
        }

        // === PUSH PHASE ===
        if (pipe.tank.isEmpty()) return;

        FluidStack toSend = pipe.tank.drain(TRANSFER_RATE, IFluidHandler.FluidAction.SIMULATE);
        if (toSend.isEmpty()) return;

        for (Direction dir : connectedDirs) {
            // TODO: Later replace with real side config (Push / Normal)
            IFluidHandler target = level.getCapability(
                    Capabilities.FluidHandler.BLOCK,
                    pos.relative(dir),
                    dir.getOpposite()
            );

            if (target == null) continue;

            int accepted = target.fill(toSend.copy(), IFluidHandler.FluidAction.EXECUTE);
            if (accepted > 0) {
                pipe.tank.drain(accepted, IFluidHandler.FluidAction.EXECUTE);
            }
        }
    }

    private static List<Direction> getConnectedDirections(BlockState state) {
        List<Direction> dirs = new ArrayList<>();
        for (Direction dir : Direction.values()) {
            if (state.getValue(LiquidPipeBlock.getConnectionProperty(dir))) {
                dirs.add(dir);
            }
        }
        return dirs;
    }

    // ===================== CAPABILITY =====================
    // This makes the pipe itself act as a fluid handler
    public IFluidHandler getFluidHandler(Direction side) {
        return tank;
    }

    // ===================== NBT =====================
    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tank.writeToNBT(registries, tag);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        tank.readFromNBT(registries, tag);
    }
}