package net.grim.mysticmachine.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.grim.mysticmachine.screen.menu.BoilerMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class BoilerScreen extends AbstractContainerScreen<BoilerMenu> {

    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath("mysticmachine", "textures/gui/container/boiler.png");

    // Bar constants
    private static final int BAR_HEIGHT = 49;
    private static final int BAR_WIDTH = 8;
    private static final int BAR_TOP = 18;
    private static final int BAR_BOTTOM = BAR_TOP + BAR_HEIGHT;

    // Water bar position (relative to GUI background)
    private static final int WATER_BAR_X = 40;

    // Steam bar position (relative to GUI background)
    private static final int STEAM_BAR_X = 128;

    // Colours
    private static final int STEAM_GREY = 0xFF888888;
    private static final int WATER_BLUE = 0xFF3366FF;
    private static final int BAR_EMPTY = 0xFF404040;

    public BoilerScreen(BoilerMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    @Override
    protected void init() {
        super.init();
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);

        // Draw the background texture
        guiGraphics.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);

        // Draw empty bar backgrounds
        guiGraphics.fill(
                leftPos + WATER_BAR_X,
                topPos + BAR_TOP,
                leftPos + WATER_BAR_X + BAR_WIDTH,
                topPos + BAR_BOTTOM,
                BAR_EMPTY
        );
        guiGraphics.fill(
                leftPos + STEAM_BAR_X,
                topPos + BAR_TOP,
                leftPos + STEAM_BAR_X + BAR_WIDTH,
                topPos + BAR_BOTTOM,
                BAR_EMPTY
        );

        // Draw water indicator (binary - full blue if adjacent to water)
        if (menu.isAdjacentToWater()) {
            guiGraphics.fill(
                    leftPos + WATER_BAR_X,
                    topPos + BAR_TOP,
                    leftPos + WATER_BAR_X + BAR_WIDTH,
                    topPos + BAR_BOTTOM,
                    WATER_BLUE
            );
        }

        // Draw steam bar (fills bottom to top based on steam amount)
        int steamFilled = menu.getSteamAmount() * BAR_HEIGHT / menu.getMaxSteam();
        if (steamFilled > 0) {
            guiGraphics.fill(
                    leftPos + STEAM_BAR_X,
                    topPos + BAR_BOTTOM - steamFilled,
                    leftPos + STEAM_BAR_X + BAR_WIDTH,
                    topPos + BAR_BOTTOM,
                    STEAM_GREY
            );
        }
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        // Draw title and inventory label
        super.renderLabels(guiGraphics, mouseX, mouseY);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        renderTooltip(guiGraphics, mouseX, mouseY);
    }
}