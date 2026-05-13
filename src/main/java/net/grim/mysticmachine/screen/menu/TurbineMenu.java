package net.grim.mysticmachine.screen.menu;

import net.grim.mysticmachine.block.ModBlocks;
import net.grim.mysticmachine.block.entity.TurbineBlockEntity;
import net.grim.mysticmachine.screen.ModMenuTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;

public class TurbineMenu extends AbstractContainerMenu {

    public final TurbineBlockEntity blockEntity;
    private final ContainerData data;
    private final ContainerLevelAccess access;

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

        // No machine slots anymore

        addPlayerInventory(inv);
        addPlayerHotbar(inv);

        addDataSlots(data);
    }

    // Steam amount scaled (for tank bar)
    public int getScaledSteam() {
        int steam = data.get(0);
        int max = data.get(1);
        int pixels = 52;
        return max == 0 ? 0 : steam * pixels / max;
    }

    // Energy bar
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

    public int getSteam() {
        return data.get(0);
    }

    public int getMaxSteam() {
        return data.get(1);
    }

    public boolean isRunning() {
        return getSteam() > 0 && getEnergy() < getMaxEnergy();
    }

    public boolean isBurning() {
        // Turbine is "active" if it has steam
        return data.get(0) > 0;
    }

    public int getScaledBurnProgress() {
        int steam = data.get(0);     // current steam
        int maxSteam = data.get(1);  // max steam
        int pixels = 52;             // matches your GUI bar height

        return maxSteam == 0 ? 0 : steam * pixels / maxSteam;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        // No machine slots → nothing to shift-click into
        return ItemStack.EMPTY;
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