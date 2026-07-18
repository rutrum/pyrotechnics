package net.rutrum.pyrotechnics.client.screen;

import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Items;

import net.rutrum.pyrotechnics.Pyrotechnics;
import net.rutrum.pyrotechnics.screen.ColorVatMenu;

import java.util.Map;
import java.util.HashMap;

public class ColorVatScreen extends AbstractContainerScreen<ColorVatMenu> {

    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(
        Pyrotechnics.MOD_ID, "textures/gui/color_vat.png");

    private static final int DYE_GRID_X = 26;
    private static final int DYE_GRID_Y = 17;
    private static final int DYE_SLOT_SIZE = 18;
    private static final int GRID_COLS = 4;
    private static final int SWATCH_SIZE = 14;

    // Lazy-init mapping from dye item to DyeColor
    private static final Map<net.minecraft.world.item.Item, DyeColor> DYE_MAP = new HashMap<>();
    private static DyeColor getDyeColor(ItemStack stack) {
        if (DYE_MAP.isEmpty()) {
            DYE_MAP.put(Items.DYE.white(), DyeColor.WHITE);
            DYE_MAP.put(Items.DYE.orange(), DyeColor.ORANGE);
            DYE_MAP.put(Items.DYE.magenta(), DyeColor.MAGENTA);
            DYE_MAP.put(Items.DYE.lightBlue(), DyeColor.LIGHT_BLUE);
            DYE_MAP.put(Items.DYE.yellow(), DyeColor.YELLOW);
            DYE_MAP.put(Items.DYE.lime(), DyeColor.LIME);
            DYE_MAP.put(Items.DYE.pink(), DyeColor.PINK);
            DYE_MAP.put(Items.DYE.gray(), DyeColor.GRAY);
            DYE_MAP.put(Items.DYE.lightGray(), DyeColor.LIGHT_GRAY);
            DYE_MAP.put(Items.DYE.cyan(), DyeColor.CYAN);
            DYE_MAP.put(Items.DYE.purple(), DyeColor.PURPLE);
            DYE_MAP.put(Items.DYE.blue(), DyeColor.BLUE);
            DYE_MAP.put(Items.DYE.brown(), DyeColor.BROWN);
            DYE_MAP.put(Items.DYE.green(), DyeColor.GREEN);
            DYE_MAP.put(Items.DYE.red(), DyeColor.RED);
            DYE_MAP.put(Items.DYE.black(), DyeColor.BLACK);
        }
        return DYE_MAP.get(stack.getItem());
    }

    private boolean fadeMode = false;

    public ColorVatScreen(ColorVatMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title, 176, 207);
        this.inventoryLabelY = 113;
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractBackground(graphics, mouseX, mouseY, partialTick);
        int x = leftPos;
        int y = (height - imageHeight) / 2;
        graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, x, y, 0f, 0f, imageWidth, imageHeight, 176, 207);
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        int x = leftPos;
        int y = topPos;

        // Draw colored swatches for each dye slot
        int baseMask = menu.getBaseMask();
        int fadeMask = menu.getFadeMask();

        for (int i = 0; i < 16; i++) {
            ItemStack dyeStack = menu.getContainer().getItem(i);
            if (dyeStack.isEmpty()) continue;

            DyeColor dyeColor = getDyeColor(dyeStack);
            if (dyeColor == null) continue;

            boolean isBase = (baseMask & (1 << i)) != 0;
            boolean isFade = (fadeMask & (1 << i)) != 0;

            int col = i % GRID_COLS;
            int row = i / GRID_COLS;
            int sx = x + DYE_GRID_X + col * DYE_SLOT_SIZE + 2;
            int sy = y + DYE_GRID_Y + row * DYE_SLOT_SIZE + 2;

            // Draw the color swatch
            int color = dyeColor.getTextureDiffuseColor() | 0xFF000000;
            graphics.fill(sx, sy, sx + SWATCH_SIZE, sy + SWATCH_SIZE, color);

            // Draw selection indicator
            if (isBase && isFade) {
                graphics.text(font, "B", sx + 1, sy + 1, 0xFFFFFF);
                graphics.text(font, "F", sx + 7, sy + 7, 0xFFFFFF);
            } else if (isBase) {
                graphics.fill(sx - 1, sy - 1, sx + SWATCH_SIZE + 1, sy, 0xFF4444);
                graphics.fill(sx - 1, sy + SWATCH_SIZE, sx + SWATCH_SIZE + 1, sy + SWATCH_SIZE + 1, 0xFF4444);
                graphics.fill(sx - 1, sy, sx, sy + SWATCH_SIZE, 0xFF4444);
                graphics.fill(sx + SWATCH_SIZE, sy, sx + SWATCH_SIZE + 1, sy + SWATCH_SIZE, 0xFF4444);
            } else if (isFade) {
                graphics.fill(sx - 1, sy - 1, sx + SWATCH_SIZE + 1, sy, 0x4444FF);
                graphics.fill(sx - 1, sy + SWATCH_SIZE, sx + SWATCH_SIZE + 1, sy + SWATCH_SIZE + 1, 0x4444FF);
                graphics.fill(sx - 1, sy, sx, sy + SWATCH_SIZE, 0x4444FF);
                graphics.fill(sx + SWATCH_SIZE, sy, sx + SWATCH_SIZE + 1, sy + SWATCH_SIZE, 0x4444FF);
            }
        }

        // Draw labels
        graphics.text(font, "Base", x + 62, y + 78, 0x404040);
        graphics.text(font, "Fade", x + 110, y + 78, 0x404040);

        // Mode indicator and toggle
        String modeText = fadeMode ? "[Fade]" : "[Base]";
        graphics.text(font, modeText, x + 62, y + 100, fadeMode ? 0x4444FF : 0xFF4444);
        graphics.text(font, "Click to toggle", x + 62, y + 110, 0x808080);

        super.extractRenderState(graphics, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean isDoubleClick) {
        double mouseX = event.x();
        double mouseY = event.y();
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        // Check if clicking on a dye swatch
        for (int i = 0; i < 16; i++) {
            ItemStack dyeStack = menu.getContainer().getItem(i);
            if (dyeStack.isEmpty()) continue;

            int col = i % GRID_COLS;
            int row = i / GRID_COLS;
            int sx = x + DYE_GRID_X + col * DYE_SLOT_SIZE + 2;
            int sy = y + DYE_GRID_Y + row * DYE_SLOT_SIZE + 2;

            if (mouseX >= sx && mouseX < sx + SWATCH_SIZE && mouseY >= sy && mouseY < sy + SWATCH_SIZE) {
                int buttonId = i | (fadeMode ? 0x100 : 0);
                if (this.minecraft != null && this.minecraft.gameMode != null) {
                    this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, buttonId);
                }
                return true;
            }
        }

        // Click on mode toggle area
        int modeX = x + 62;
        int modeY = y + 96;
        if (mouseX >= modeX && mouseX < modeX + 60 && mouseY >= modeY && mouseY < modeY + 20) {
            fadeMode = !fadeMode;
            return true;
        }

        return super.mouseClicked(event, isDoubleClick);
    }

    @Override
    protected void init() {
        super.init();
        titleLabelX = (imageWidth - font.width(title)) / 2;
    }
}