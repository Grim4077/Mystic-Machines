package net.grim.mysticmachine.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.minecraft.tags.ItemTags;

public class BoilerBlockEntity extends BlockEntity {

    // Fluid containers
    //private final FluidTank waterTank = new FluidTank(8000);
    private final FluidTank steamTank = new FluidTank(8000);

    private int litTime = 0;
    private int steamTickRate = 2;

    private final ItemStackHandler itemHandler = new ItemStackHandler(1) {
        @Override
        public boolean isItemValid(int slot, ItemStack stack)
        {
            return stack.is(ItemTags.COALS);
        }
    };

    public BoilerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.BOILER_BE.get(), pos, state);
    }

    // Called every server tick via the ticker in BoilerBlock
    public void tick(Level level, BlockPos pos, BlockState state) {
        if (level.isClientSide()) return;

        boolean saveFlag = false;
        
        if (isLit()) {
            produceSteam();
            --litTime;
        }

        if (!isLit() && canProduceSteam(level,pos)) {

            saveFlag = true;

            ItemStack stack = itemHandler.getStackInSlot(0);

            litTime = stack.getBurnTime(null);

            if (isLit()) {
                stack.shrink(1);
            }
        }

        if (saveFlag)
        {
            setChanged();
        }

    }

    // Save data when the chunk is saved
    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);

        tag.putInt("LitTime", litTime);
        steamTank.writeToNBT(registries,tag);

        tag.put("Inventory", itemHandler.serializeNBT(registries));
    }

    // Load data when the chunk loads
    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);

        litTime = tag.getInt("LitTime");
        steamTank.readFromNBT(registries,tag);

        itemHandler.deserializeNBT(registries, tag.getCompound("Inventory"));
    }

    private boolean isAdjacentToWater(Level level, BlockPos pos)
    {
        for (Direction dir : Direction.values())
        {
            if (level.getFluidState(pos.relative(dir)).is(FluidTags.WATER))
                return true;
        }

        return false;
    }

    private boolean canProduceSteam(Level level, BlockPos pos)
    {
        if (!itemHandler.getStackInSlot(0).isEmpty() && itemHandler.isItemValid(0,itemHandler.getStackInSlot(0)) && steamTank.getFluidAmount() < steamTank.getCapacity()) { //Checks if coal is present and steam reserve has space
            return isAdjacentToWater(level, pos); //Checks if boiler is next to water block
        }

        return false;
    }

    private void produceSteam()
    {
        steamTank.fill(new FluidStack(Fluids.WATER, steamTickRate), IFluidHandler.FluidAction.EXECUTE); //TODO Add steam later to replace water
    }

    private boolean isLit()
    {
        return this.litTime > 0;
    }
}