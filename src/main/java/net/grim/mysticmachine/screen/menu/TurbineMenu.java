package net.grim.mysticmachine.screen.menu;

import net.grim.mysticmachine.block.ModBlocks;
import net.grim.mysticmachine.block.entity.TurbineBlockEntity;
import net.grim.mysticmachine.screen.ModMenuTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.items.SlotItemHandler;

public class TurbineMenu extends AbstractContainerMenu {

    public final TurbineBlockEntity blockEntity;
    private final ContainerData data;
    private final ContainerLevelAccess access;

    private static final int MACHINE_SLOTS = 2;
    private static final int PLAYER_INV_START = 2;
    private static final int PLAYER_INV_END = 38;

    // CLIENT
    public TurbineMenu(int id, Inventory inv, FriendlyByteBuf buf) {
        this(id, inv,
                (TurbineBlockEntity) inv.player.level().getBlockEntity(buf.readBlockPos()),
                new SimpleContainerData(4));
    }

    // COMMON
    public TurbineMenu(int id, Inventory inv, TurbineBlockEntity be, ContainerData data) {
        super(ModMenuTypes.TURBINE_MENU.get(), id);

        this.blockEntity = be;
        this.data = data;
        this.access = ContainerLevelAccess.create(be.getLevel(), be.getBlockPos());

        addMachineSlots();
        addPlayerInventory(inv);
        addPlayerHotbar(inv);

        addDataSlots(data);
    }

    private void addMachineSlots() {
        // INPUT (coal)
        this.addSlot(new SlotItemHandler(blockEntity.itemHandler, 0, 54, 34));

        // OUTPUT (energy machines often still use item output)
        this.addSlot(new SlotItemHandler(blockEntity.itemHandler, 1, 80, 59));
    }

    public boolean isBurning() {
        return data.get(0) > 0;
    }

    public int getScaledBurnProgress() {
        int burn = data.get(0);
        int max = data.get(1);
        int pixels = 13;
        return max == 0 ? 0 : burn * pixels / max;
    }

    public int getScaledEnergy() {
        int energy = data.get(2);
        int max = data.get(3);
        int pixels = 52;
        return max == 0 ? 0 : energy * pixels / max;
    }

    public int getEnergy() {
        return data.get(2);
    }

    public int getMaxEnergy() {
        return data.get(3);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack result = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);

        if (slot != null && slot.hasItem()) {
            ItemStack stack = slot.getItem();
            result = stack.copy();

            // MACHINE → PLAYER
            if (index < 2) {
                if (!this.moveItemStackTo(stack, 2, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            }
            // PLAYER → MACHINE
            else {
                if (stack.is(Items.COAL)) {
                    if (!this.moveItemStackTo(stack, 0, 1, false)) {
                        return ItemStack.EMPTY;
                    }
                }
            }

            if (stack.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }

        return result;
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(access, player, ModBlocks.MACHINE_TURBINE.get());
    }

    private void addPlayerInventory(Inventory inv) {
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(inv,
                        col + row * 9 + 9,
                        8 + col * 18,
                        84 + row * 18));
            }
        }
    }

    private void addPlayerHotbar(Inventory inv) {
        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(inv, col, 8 + col * 18, 142));
        }
    }
}