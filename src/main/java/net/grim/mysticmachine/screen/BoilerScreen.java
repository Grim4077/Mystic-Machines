package net.grim.mysticmachine.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.grim.mysticmachine.MysticMachine;
import net.grim.mysticmachine.screen.menu.BoilerMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class BoilerScreen extends AbstractContainerScreen<BoilerMenu> {

    private static final ResourceLocation GUI_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(MysticMachine.MOD_ID,
                    "textures/gui/container/boiler.png");

    private static final ResourceLocation STEAM_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(MysticMachine.MOD_ID,
                    "textures/liquids/steam.png");

    private static final ResourceLocation WATER_TEXTURE =
            ResourceLocation.fromNamespaceAndPath("minecraft",
                    "textures/block/water_still.png");

    // Bar constants
    private static final int BAR_HEIGHT = 53;

    // Left bar (water)
    private static final int WATER_BAR_X = 33;
    private static final int WATER_BAR_Y = 16;
    private static final int WATER_BAR_WIDTH = 14;

    // Right bar (steam)
    private static final int STEAM_BAR_X = 129;
    private static final int STEAM_BAR_Y = 17;
    private static final int STEAM_BAR_WIDTH = 14;

    public BoilerScreen(BoilerMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    @Override
    protected void init() {
        super.init();
        this.titleLabelX = (imageWidth - font.width(title)) / 2;
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        // Draw background
        guiGraphics.blit(GUI_TEXTURE, x, y, 0, 0, imageWidth, imageHeight);

        // Draw water bar
        renderWaterBar(guiGraphics, x, y);

        // Draw steam bar
        renderSteamBar(guiGraphics, x, y);
    }

    private void renderWaterBar(GuiGraphics guiGraphics, int x, int y) {
        if (menu.isAdjacentToWater()) {
            RenderSystem.setShaderColor(0.24F, 0.46F, 0.72F, 1.0F);

            int remainingHeight = BAR_HEIGHT;
            int currentY = y + WATER_BAR_Y;

            while (remainingHeight > 0) {
                int renderHeight = Math.min(remainingHeight, 16);
                int remainingWidth = WATER_BAR_WIDTH;
                int currentX = x + WATER_BAR_X;

                while (remainingWidth > 0) {
                    int renderWidth = Math.min(remainingWidth, 16);
                    guiGraphics.blit(WATER_TEXTURE,
                            currentX,
                            currentY,
                            0, 0,
                            renderWidth,
                            renderHeight,
                            16, 16);
                    currentX += renderWidth;
                    remainingWidth -= renderWidth;
                }

                currentY += renderHeight;
                remainingHeight -= renderHeight;
            }

            // Reset colour after so other elements aren't tinted
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        }
    }

    private void renderSteamBar(GuiGraphics guiGraphics, int x, int y) {
        int steamHeight = menu.getSteamAmount() * BAR_HEIGHT / menu.getMaxSteam();
        if (steamHeight > 0) {
            int offset = BAR_HEIGHT - steamHeight;

            guiGraphics.blit(STEAM_TEXTURE,
                    x + STEAM_BAR_X,
                    y + STEAM_BAR_Y + offset,
                    0, offset,
                    STEAM_BAR_WIDTH,
                    steamHeight,
                    14, BAR_HEIGHT);
        }
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        super.renderLabels(guiGraphics, mouseX, mouseY);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void renderTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        if (this.menu.getCarried().isEmpty()) {

            // Water bar tooltip
            if (isHovering(WATER_BAR_X, WATER_BAR_Y, WATER_BAR_WIDTH, BAR_HEIGHT, mouseX, mouseY)) {
                guiGraphics.renderTooltip(this.font,
                        Component.literal(menu.isAdjacentToWater() ? "Water: Present" : "Water: Not Present"),
                        mouseX, mouseY);
            }

            // Steam bar tooltip
            if (isHovering(STEAM_BAR_X, STEAM_BAR_Y, STEAM_BAR_WIDTH, BAR_HEIGHT, mouseX, mouseY)) {
                guiGraphics.renderTooltip(this.font,
                        Component.literal(menu.getSteamAmount() + " / " + menu.getMaxSteam() + " mB"),
                        mouseX, mouseY);
            }
        }
        super.renderTooltip(guiGraphics, mouseX, mouseY);
    }
}