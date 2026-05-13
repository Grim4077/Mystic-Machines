package net.grim.mysticmachine.block.entity;

import net.grim.mysticmachine.block.entity.renderer.ModBlockEntities;
import net.grim.mysticmachine.screen.menu.TurbineMenu;
import net.grim.mysticmachine.util.CustomEnergy;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

public class TurbineBlockEntity extends BlockEntity implements MenuProvider {

    public TurbineBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.TURBINE_BE.get(), pos, blockState);
        this.data = createContainerData();
    }

    public final ItemStackHandler itemHandler = new ItemStackHandler(2) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
            if (level != null && !level.isClientSide()) {
                level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
            }
        }
    };

    private static final int INPUT_SLOT = 0;
    private static final int OUTPUT_SLOT = 1;
    private static final int FE_PER_TICK = 20;

    private int burnTime = 0;
    private int maxBurnTime = 0;

    private final CustomEnergy ENERGY_STORAGE = new CustomEnergy(25000, 40, 40) {
        @Override
        public void onContentsChanged() {
            setChanged();
        }
    };

    protected final ContainerData data;

    private ContainerData createContainerData() {
        return new ContainerData() {
            @Override
            public int get(int i) {
                return switch (i) {
                    case 0 -> burnTime;
                    case 1 -> maxBurnTime;
                    case 2 -> ENERGY_STORAGE.getEnergyStored();
                    case 3 -> ENERGY_STORAGE.getMaxEnergyStored();
                    default -> 0;
                };
            }

            @Override
            public void set(int i, int value) {
                switch (i) {
                    case 0 -> burnTime = value;
                    case 1 -> maxBurnTime = value;
                    case 2 -> ENERGY_STORAGE.setEnergy(value);
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
        ItemStack fuel = itemHandler.getStackInSlot(INPUT_SLOT);

        if (burnTime <= 0 && isFuel(fuel)) {
            burnTime = getFuelBurnTime(fuel);
            maxBurnTime = burnTime;
            itemHandler.extractItem(INPUT_SLOT, 1, false);
            dirty = true;
        }

        if (burnTime > 0) {
            burnTime--;
            ENERGY_STORAGE.receiveEnergy(FE_PER_TICK, false);
            dirty = true;
        }

        if (dirty) setChanged(level, pos, state);
    }

    private boolean isFuel(ItemStack stack) {
        return stack.is(Items.COAL);
    }

    private int getFuelBurnTime(ItemStack stack) {
        return stack.is(Items.COAL) ? 1600 : 0;
    }

    public void drops() {
        SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            inventory.setItem(i, itemHandler.getStackInSlot(i));
        }
        Containers.dropContents(this.level, this.worldPosition, inventory);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("inventory", itemHandler.serializeNBT(registries));
        tag.putInt("burnTime", burnTime);
        tag.putInt("maxBurnTime", maxBurnTime);
        tag.putInt("energy", ENERGY_STORAGE.getEnergyStored());
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        itemHandler.deserializeNBT(registries, tag.getCompound("inventory"));
        burnTime = tag.getInt("burnTime");
        maxBurnTime = tag.getInt("maxBurnTime");
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