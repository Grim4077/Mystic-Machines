package net.grim.mysticmachine.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.grim.mysticmachine.MysticMachine;
import net.grim.mysticmachine.screen.menu.TurbineMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class TurbineScreen extends AbstractContainerScreen<TurbineMenu> {

    private static final ResourceLocation GUI_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(MysticMachine.MOD_ID,
                    "textures/gui/turbine/turbine_gui.png");

    private static final ResourceLocation ENERGY_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(MysticMachine.MOD_ID,
                    "textures/gui/vertical_power.png");

    private static final ResourceLocation STEAM_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(MysticMachine.MOD_ID,
                    "textures/liquids/steam.png");

    public TurbineScreen(TurbineMenu menu, Inventory playerInventory, Component title) {
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

        // Render Steam/Burn Bar
        renderSteamBar(guiGraphics, x, y);

        // Render Energy Bar
        renderEnergyBar(guiGraphics, x, y);
    }

    private void renderSteamBar(GuiGraphics guiGraphics, int x, int y) {
        if (menu.isBurning()) {
            int steamHeight = menu.getScaledBurnProgress();
            int maxBarHeight = 52;
            int offset = maxBarHeight - steamHeight;

            // Texture width/height
            guiGraphics.blit(STEAM_TEXTURE, x + 17, y + 11 + offset, 0, offset, 14, steamHeight, 14, maxBarHeight);
        }
    }

    private void renderEnergyBar(GuiGraphics guiGraphics, int x, int y) {
        int energyHeight = menu.getScaledEnergy();
        int maxBarHeight = 52;
        int offset = maxBarHeight - energyHeight;

        guiGraphics.blit(ENERGY_TEXTURE, x + 145, y + 11 + offset, 0, offset, 14, energyHeight, 14, maxBarHeight);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void renderTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        // Only render custom tooltips if we aren't dragging an item
        if (this.menu.getCarried().isEmpty()) {
            // Energy Tooltip (X: 145, Y: 11, W: 14, H: 52)
            if (isHovering(145, 11, 14, 52, mouseX, mouseY)) {
                guiGraphics.renderTooltip(this.font,
                        Component.literal(menu.getEnergy() + " / " + menu.getMaxEnergy() + " FE"),
                        mouseX, mouseY);
            }

            // Steam Tooltip (X: 17, Y: 11, W: 14, H: 52)
            if (isHovering(17, 11, 14, 52, mouseX, mouseY)) {
                guiGraphics.renderTooltip(this.font,
                        Component.literal("Generating Power"),
                        mouseX, mouseY);
            }
        }
        super.renderTooltip(guiGraphics, mouseX, mouseY);
    }
}