package net.grim.mysticmachine.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemStackHandler;

public class BoilerBlockEntity extends BlockEntity {

    // Add item/fluid storage fields here later, e.g.:
    private final ItemStackHandler itemHandler = new ItemStackHandler(1);
    // private final FluidTank waterTank = new FluidTank(8000);
    // private final FluidTank steamTank = new FluidTank(8000);

    private int progress = 0;
    private int maxProgress = 100;

    public BoilerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.BOILER_BE.get(), pos, state);
    }

    // Called every server tick via the ticker in BoilerBlock
    public void tick(Level level, BlockPos pos, BlockState state) {
        if (level.isClientSide()) return;



    }

    // Save data when the chunk is saved
    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt("Progress", progress);

        tag.put("Inventory", itemHandler.serializeNBT(registries));
    }

    // Load data when the chunk loads
    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        progress = tag.getInt("Progress");

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
}