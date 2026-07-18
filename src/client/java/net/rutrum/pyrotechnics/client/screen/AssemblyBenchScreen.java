package net.rutrum.pyrotechnics.client.screen;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

import net.rutrum.pyrotechnics.Pyrotechnics;
import net.rutrum.pyrotechnics.screen.AssemblyBenchMenu;

public class AssemblyBenchScreen extends AbstractContainerScreen<AssemblyBenchMenu> {

    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(
        Pyrotechnics.MOD_ID, "textures/gui/assembly_bench.png");

    public AssemblyBenchScreen(AssemblyBenchMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractBackground(graphics, mouseX, mouseY, partialTick);
        int x = leftPos;
        int y = (height - imageHeight) / 2;
        graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, x, y, 0f, 0f, imageWidth, imageHeight, 176, 174);
    }

    @Override
    protected void init() {
        super.init();
        titleLabelX = (imageWidth - font.width(title)) / 2;
    }
}