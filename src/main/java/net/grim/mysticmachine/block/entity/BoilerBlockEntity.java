package net.grim.mysticmachine.block.entity;

import net.grim.mysticmachine.block.entity.renderer.ModBlockEntities;
import net.grim.mysticmachine.fluid.ModFluids;
import net.grim.mysticmachine.screen.menu.BoilerMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

public class BoilerBlockEntity extends BlockEntity implements MenuProvider {

    public final FluidTank steamTank = new FluidTank(8000) {
        @Override
        public boolean isFluidValid(FluidStack stack) {
            return stack.getFluid() == ModFluids.STEAM.get();
        }
    };

    public final ItemStackHandler itemHandler = new ItemStackHandler(1) {
        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            return stack.is(ItemTags.COALS);
        }
    };

    private int litTime = 0;
    private int steamTickRate = 50000;

    // ContainerData syncs these values to the client automatically
    protected final ContainerData data = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> litTime;
                case 1 -> steamTank.getFluidAmount();
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case 0 -> litTime = value;
                case 1 -> {} // steamTank is read-only from client side
            }
        }

        @Override
        public int getCount() {
            return 2;
        }
    };

    public BoilerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.BOILER_BE.get(), pos, state);
    }

    // MenuProvider implementation
    @Override
    public Component getDisplayName() {
        return Component.translatable("block.mysticmachine.machine_boiler");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new BoilerMenu(containerId, playerInventory, this, this.data);
    }

    public void tick(Level level, BlockPos pos, BlockState state) {
        if (level.isClientSide()) return;

        boolean saveFlag = false;

        if (isLit()) {
            produceSteam();
            --litTime;
        }

        if (!isLit() && canProduceSteam(level, pos)) {
            saveFlag = true;

            ItemStack stack = itemHandler.getStackInSlot(0);
            litTime = stack.getBurnTime(null);

            if (isLit()) {
                stack.shrink(1);
            }
        }

       if (steamTank.getFluidAmount() > 0) {
          transferSteam(level, pos);
         saveFlag = true;
       }

        if (saveFlag) {
            setChanged();
        }
    }
    private void produceSteam() {
        steamTank.fill(
                new FluidStack(ModFluids.STEAM.get(), steamTickRate),
                IFluidHandler.FluidAction.EXECUTE
        );
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt("LitTime", litTime);
        steamTank.writeToNBT(registries, tag);
        tag.put("Inventory", itemHandler.serializeNBT(registries));
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        litTime = tag.getInt("LitTime");
        steamTank.readFromNBT(registries, tag);
        itemHandler.deserializeNBT(registries, tag.getCompound("Inventory"));
    }



    public boolean isAdjacentToWater(Level level, BlockPos pos) {
        for (Direction dir : Direction.values()) {
            if (level.getFluidState(pos.relative(dir)).is(FluidTags.WATER))
                return true;
        }
        return false;
    }

    private boolean canProduceSteam(Level level, BlockPos pos) {
        if (!itemHandler.getStackInSlot(0).isEmpty() && itemHandler.isItemValid(0, itemHandler.getStackInSlot(0)) && steamTank.getFluidAmount() < steamTank.getCapacity()) {
            return isAdjacentToWater(level, pos);
        }
        return false;
    }

    private void transferSteam(Level level, BlockPos pos) {

        int amount = Math.min(steamTank.getFluidAmount(), 100);
        if (amount <= 0) return;

        FluidStack stack = new FluidStack(ModFluids.STEAM.get(), amount);

        for (Direction dir : Direction.values()) {

            IFluidHandler handler = level.getCapability(
                    Capabilities.FluidHandler.BLOCK,
                    pos.relative(dir),
                    null
            );

            if (handler == null) continue;

            int accepted = handler.fill(stack, IFluidHandler.FluidAction.EXECUTE);

            if (accepted > 0) {
                steamTank.drain(accepted, IFluidHandler.FluidAction.EXECUTE);
                stack.shrink(accepted);
            }

            if (stack.isEmpty()) return;
        }
    }
    public boolean isLit() {
        return this.litTime > 0;
    }
}