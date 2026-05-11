package net.grim.mysticmachine.screen.menu;

import net.grim.mysticmachine.block.entity.BoilerBlockEntity;
import net.grim.mysticmachine.screen.ModMenuTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.items.SlotItemHandler;

public class BoilerMenu extends AbstractContainerMenu {

    public final BoilerBlockEntity blockEntity;
    private final ContainerData data;

    // Client-side constructor - called via IMenuTypeExtension
    public BoilerMenu(int containerId, Inventory playerInventory, FriendlyByteBuf buf) {
        this(containerId, playerInventory,
                (BoilerBlockEntity) playerInventory.player.level().getBlockEntity(buf.readBlockPos()),
                new SimpleContainerData(2));
    }

    // Server-side constructor - called when block is right-clicked
    public BoilerMenu(int containerId, Inventory playerInventory, BoilerBlockEntity blockEntity, ContainerData data) {
        super(ModMenuTypes.BOILER_MENU.get(), containerId);
        this.blockEntity = blockEntity;
        this.data = data;

        // Coal input slot - coordinates relative to GUI background
        addSlot(new SlotItemHandler(blockEntity.itemHandler, 0, 79, 31));

        // Player inventory slots (27 slots, 3 rows of 9)
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
            }
        }

        // Player hotbar slots (9 slots)
        for (int col = 0; col < 9; col++) {
            addSlot(new Slot(playerInventory, col, 8 + col * 18, 142));
        }

        // Sync litTime and steamAmount to client
        addDataSlots(data);
    }

    // Getters for the screen to use
    public int getLitTime() {
        return data.get(0);
    }

    public int getSteamAmount() {
        return data.get(1);
    }

    public int getMaxSteam() {
        return blockEntity.steamTank.getCapacity();
    }

    public boolean isAdjacentToWater() {
        return blockEntity.isAdjacentToWater(blockEntity.getLevel(), blockEntity.getBlockPos());
    }

    // Check player is still close enough to keep menu open
    @Override
    public boolean stillValid(Player player) {
        return this.blockEntity.getLevel() != null &&
                player.distanceToSqr(
                        this.blockEntity.getBlockPos().getX() + 0.5,
                        this.blockEntity.getBlockPos().getY() + 0.5,
                        this.blockEntity.getBlockPos().getZ() + 0.5
                ) <= 64.0; // 64 = 8 blocks squared
    }

    // Handle shift-clicking items into the menu
    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack stack = ItemStack.EMPTY;
        Slot slot = slots.get(index);

        if (slot.hasItem()) {
            ItemStack slotStack = slot.getItem();
            stack = slotStack.copy();

            // If shift clicking from machine slot, move to player inventory
            if (index < 1) {
                if (!moveItemStackTo(slotStack, 1, slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
                // If shift clicking from player inventory, move to machine slot
            } else {
                if (!moveItemStackTo(slotStack, 0, 1, false)) {
                    return ItemStack.EMPTY;
                }
            }

            if (slotStack.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }

        return stack;
    }
}