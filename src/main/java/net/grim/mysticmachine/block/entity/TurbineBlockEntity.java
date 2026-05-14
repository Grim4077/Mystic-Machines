package net.grim.mysticmachine.block.entity;

import net.grim.mysticmachine.block.entity.renderer.ModBlockEntities;
import net.grim.mysticmachine.fluid.ModFluids;
import net.grim.mysticmachine.screen.menu.TurbineMenu;
import net.grim.mysticmachine.util.CustomEnergy;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;


import javax.annotation.Nullable;

public class TurbineBlockEntity extends BlockEntity implements MenuProvider {

    private static final int STEAM_PER_TICK = 5;
    private static final int FE_PER_TICK = 40;

    // 🔥 Steam tank
    public final FluidTank steamTank = new FluidTank(8000) {
        @Override
        public boolean isFluidValid(FluidStack stack) {
            return stack.getFluid() == ModFluids.STEAM.get();
        }

        @Override
        protected void onContentsChanged() {
            setChanged();
        }
    };

    // ⚡ Energy storage
    private final CustomEnergy ENERGY_STORAGE = new CustomEnergy(25000, 40, 40) {
        @Override
        public void onContentsChanged() {
            setChanged();
        }
    };

    protected final ContainerData data;

    public TurbineBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.TURBINE_BE.get(), pos, state);
        this.data = createContainerData();
    }

    // ===================== TICK =====================

    public void tick(Level level, BlockPos pos, BlockState state) {
        if (level.isClientSide) return;

        boolean dirty = false;


        // 1. PULL STEAM FROM WORLD
        for (Direction dir : Direction.values()) {

            IFluidHandler from = level.getCapability(
                    Capabilities.FluidHandler.BLOCK,
                    pos.relative(dir),
                    null
            );

            if (from == null) continue;

            FluidStack sim = from.drain(STEAM_PER_TICK, IFluidHandler.FluidAction.SIMULATE);

            if (sim.isEmpty()) continue;

            FluidStack drained = from.drain(STEAM_PER_TICK, IFluidHandler.FluidAction.EXECUTE);

            if (!drained.isEmpty()) {
                steamTank.fill(drained, IFluidHandler.FluidAction.EXECUTE);
                dirty = true;
                break;
            }
        }


        // 2. CONVERT STEAM → ENERGY
        if (steamTank.getFluidAmount() >= STEAM_PER_TICK
                && ENERGY_STORAGE.getEnergyStored() < ENERGY_STORAGE.getMaxEnergyStored()) {

            steamTank.drain(STEAM_PER_TICK, IFluidHandler.FluidAction.EXECUTE);
            ENERGY_STORAGE.receiveEnergy(FE_PER_TICK, false);

            dirty = true;
        }

        if (dirty) {
            setChanged(level, pos, state);
        }
    }

    // ===================== UI =====================

    private ContainerData createContainerData() {
        return new ContainerData() {
            @Override
            public int get(int i) {
                return switch (i) {
                    case 0 -> steamTank.getFluidAmount();
                    case 1 -> steamTank.getCapacity();
                    case 2 -> ENERGY_STORAGE.getEnergyStored();
                    case 3 -> ENERGY_STORAGE.getMaxEnergyStored();
                    default -> 0;
                };
            }

            @Override
            public void set(int i, int value) {}

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

    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inv, Player player) {
        return new TurbineMenu(id, inv, this, this.data);
    }

    // ===================== CAPABILITIES (IMPORTANT FIX) =====================

    /**
     * THIS is the NeoForge-correct way (NOT getCapability override)
     */
    public static void registerCapabilities(RegisterCapabilitiesEvent event) {

        event.registerBlockEntity(
                Capabilities.FluidHandler.BLOCK,
                ModBlockEntities.TURBINE_BE.get(),
                (be, side) -> be.steamTank
        );

        event.registerBlockEntity(
                Capabilities.EnergyStorage.BLOCK,
                ModBlockEntities.TURBINE_BE.get(),
                (be, side) -> be.ENERGY_STORAGE
        );
    }

    // ===================== ACCESSORS =====================

    public CustomEnergy getEnergyStorage() {
        return ENERGY_STORAGE;
    }

    public IFluidHandler getSteamTank() {
        return steamTank;
    }

    public boolean isBurning() {
        return steamTank.getFluidAmount() > 0;
    }

    // ===================== SAVE / LOAD =====================

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

    // ===================== SYNC =====================

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return saveWithoutMetadata(registries);
    }
}