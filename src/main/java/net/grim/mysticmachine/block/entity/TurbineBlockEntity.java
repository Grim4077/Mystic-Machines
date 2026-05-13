package net.grim.mysticmachine.block.entity;

import net.grim.mysticmachine.block.entity.renderer.ModBlockEntities;
import net.grim.mysticmachine.fluid.ModFluids;
import net.grim.mysticmachine.screen.menu.TurbineMenu;
import net.grim.mysticmachine.util.CustomEnergy;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.Nullable;

public class TurbineBlockEntity extends BlockEntity implements MenuProvider {

    // Steam consumption and energy production constants
    private static final int STEAM_PER_TICK = 5;
    private static final int FE_PER_TICK = 40;

    // Steam tank - only accepts mysticmachine steam
    public final FluidTank steamTank = new FluidTank(8000) {
        @Override
        public boolean isFluidValid(FluidStack stack) {
            return stack.getFluid() == ModFluids.STEAM.get();
        }
    };

    // Energy storage
    private final CustomEnergy ENERGY_STORAGE = new CustomEnergy(25000, 40, 40) {
        @Override
        public void onContentsChanged() {
            setChanged();
        }
    };

    protected final ContainerData data;

    public TurbineBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.TURBINE_BE.get(), pos, blockState);
        this.data = createContainerData();
    }

    private ContainerData createContainerData() {
        return new ContainerData() {
            @Override
            public int get(int i) {
                return switch (i) {
                    case 0 -> steamTank.getFluidAmount();  // Current steam
                    case 1 -> steamTank.getCapacity();     // Max steam
                    case 2 -> ENERGY_STORAGE.getEnergyStored();
                    case 3 -> ENERGY_STORAGE.getMaxEnergyStored();
                    default -> 0;
                };
            }

            @Override
            public void set(int i, int value) {
                switch (i) {
                    case 2 -> ENERGY_STORAGE.setEnergy(value);
                    // Steam tank is read only from client side
                }
            }

            @Override
            public int getCount() {
                return 4;
            }
        };
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.mysticmachine.machine_turbine");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {
        return new TurbineMenu(i, inventory, this, this.data);
    }

    public void tick(Level level, BlockPos pos, BlockState state) {
        if (level.isClientSide()) return;

        boolean dirty = false;

        // If we have steam and energy storage isn't full, consume steam and produce FE
        if (steamTank.getFluidAmount() >= STEAM_PER_TICK &&
                ENERGY_STORAGE.getEnergyStored() < ENERGY_STORAGE.getMaxEnergyStored()) {

            // Drain steam from tank
            steamTank.drain(new FluidStack(ModFluids.STEAM.get(), STEAM_PER_TICK),
                    IFluidHandler.FluidAction.EXECUTE);

            // Produce energy
            ENERGY_STORAGE.receiveEnergy(FE_PER_TICK, false);

            dirty = true;
        }

        if (dirty) setChanged(level, pos, state);
    }

    // Used by the screen to determine if the turbine is actively generating
    public boolean isBurning() {
        return steamTank.getFluidAmount() > 0;
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        steamTank.writeToNBT(registries, tag);
        tag.putInt("energy", ENERGY_STORAGE.getEnergyStored());
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        steamTank.readFromNBT(registries, tag);
        ENERGY_STORAGE.setEnergy(tag.getInt("energy"));
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return saveWithoutMetadata(registries);
    }
}