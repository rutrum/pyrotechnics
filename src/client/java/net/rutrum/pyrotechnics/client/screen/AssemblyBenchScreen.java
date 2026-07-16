package net.rutrum.pyrotechnics.client.screen;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
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
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        // Draw the background
        extractBackground(graphics, mouseX, mouseY, partialTick);

        // Draw the GUI container texture
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
        graphics.blit(TEXTURE, x, y, imageWidth, imageHeight, 0, 0, imageWidth, imageHeight);

        // Let the parent draw slots and items
        super.extractRenderState(graphics, mouseX, mouseY, partialTick);
    }

    @Override
    protected void init() {
        super.init();
        titleLabelX = (imageWidth - font.width(title)) / 2;
    }
}