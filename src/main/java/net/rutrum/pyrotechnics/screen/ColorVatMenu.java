package net.rutrum.pyrotechnics.screen;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.component.FireworkExplosion;
import net.minecraft.core.component.DataComponents;

import net.rutrum.pyrotechnics.Pyrotechnics;

import java.util.Map;
import java.util.HashMap;

public class ColorVatMenu extends AbstractContainerMenu {

    private static final int DYE_START = 0;
    private static final int DYE_END = 15;
    private static final int GUNPOWDER_SLOT = 16;
    private static final int RESULT_SLOT = 17;
    private static final int CONTAINER_SIZE = 18;

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

    private final Container container;
    private final DataSlot baseMaskData;
    private final DataSlot fadeMaskData;

    // Client constructor
    public ColorVatMenu(int syncId, Inventory playerInventory) {
        this(syncId, playerInventory, new SimpleContainer(CONTAINER_SIZE));
    }

    // Server constructor
    public ColorVatMenu(int syncId, Inventory playerInventory, Container container) {
        super(Pyrotechnics.COLOR_VAT_MENU, syncId);
        this.container = container;
        container.startOpen(playerInventory.player);

        this.baseMaskData = addDataSlot(DataSlot.standalone());
        this.fadeMaskData = addDataSlot(DataSlot.standalone());

        // 4x4 dye grid (slots 0-15)
        for (int i = 0; i < 16; i++) {
            int col = i % 4;
            int row = i / 4;
            this.addSlot(new Slot(container, DYE_START + i, 26 + col * 18, 17 + row * 18) {
                @Override
                public boolean mayPlace(ItemStack stack) {
                    return getDyeColor(stack) != null;
                }
            });
        }

        // Gunpowder (slot 16)
        this.addSlot(new Slot(container, GUNPOWDER_SLOT, 26, 93) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return stack.is(Items.GUNPOWDER);
            }
        });

        // Result (slot 17)
        this.addSlot(new Slot(container, RESULT_SLOT, 134, 53) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return false;
            }
            @Override
            public void onTake(Player player, ItemStack stack) {
                onTakeOutput(player, stack);
            }
        });

        // Player inventory
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 125 + row * 18));
            }
        }
        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(playerInventory, col, 8 + col * 18, 183));
        }
    }

    @Override
    public boolean clickMenuButton(Player player, int id) {
        int slotIndex = id & 0xFF;
        boolean isFade = (id & 0x100) != 0;
        if (slotIndex < 0 || slotIndex >= 16) return false;

        int mask = isFade ? fadeMaskData.get() : baseMaskData.get();
        mask ^= (1 << slotIndex);
        if (isFade) fadeMaskData.set(mask);
        else baseMaskData.set(mask);

        slotsChanged(container);
        return true;
    }

    private void onTakeOutput(Player player, ItemStack resultStack) {
        container.getItem(GUNPOWDER_SLOT).shrink(1);

        int combinedMask = baseMaskData.get() | fadeMaskData.get();
        for (int i = 0; i < 16; i++) {
            if ((combinedMask & (1 << i)) != 0) {
                container.getItem(DYE_START + i).shrink(1);
            }
        }

        baseMaskData.set(0);
        fadeMaskData.set(0);
        container.setChanged();
    }

    private ItemStack buildStar() {
        if (container.getItem(GUNPOWDER_SLOT).isEmpty()) return ItemStack.EMPTY;

        int baseMask = baseMaskData.get();
        int fadeMask = fadeMaskData.get();
        if (baseMask == 0 && fadeMask == 0) return ItemStack.EMPTY;

        IntList colors = new IntArrayList();
        IntList fadeColors = new IntArrayList();

        for (int i = 0; i < 16; i++) {
            ItemStack dyeStack = container.getItem(DYE_START + i);
            if (dyeStack.isEmpty()) continue;
            DyeColor dyeColor = getDyeColor(dyeStack);
            if (dyeColor == null) continue;

            int fc = dyeColor.getFireworkColor();
            if ((baseMask & (1 << i)) != 0) colors.add(fc);
            if ((fadeMask & (1 << i)) != 0) fadeColors.add(fc);
        }

        if (colors.isEmpty()) return ItemStack.EMPTY;

        FireworkExplosion explosion = new FireworkExplosion(
            FireworkExplosion.Shape.SMALL_BALL, colors, fadeColors, false, false);

        ItemStack result = new ItemStack(Items.FIREWORK_STAR, 1);
        result.set(DataComponents.FIREWORK_EXPLOSION, explosion);
        return result;
    }

    @Override
    public void slotsChanged(Container inventory) {
        super.slotsChanged(inventory);
        if (inventory == this.container) {
            container.setItem(RESULT_SLOT, buildStar());
        }
    }

    @Override
    public ItemStack quickMoveStack(Player player, int slotIndex) {
        ItemStack newStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(slotIndex);
        if (slot != null && slot.hasItem()) {
            ItemStack originalStack = slot.getItem();
            newStack = originalStack.copy();

            if (slotIndex == RESULT_SLOT) {
                if (!moveItemStackTo(originalStack, CONTAINER_SIZE, this.slots.size(), true))
                    return ItemStack.EMPTY;
                slot.onQuickCraft(originalStack, newStack);
            } else if (slotIndex < CONTAINER_SIZE) {
                if (!moveItemStackTo(originalStack, CONTAINER_SIZE, this.slots.size(), true))
                    return ItemStack.EMPTY;
            } else {
                if (originalStack.is(Items.GUNPOWDER)) {
                    if (!moveItemStackTo(originalStack, GUNPOWDER_SLOT, GUNPOWDER_SLOT + 1, false))
                        return ItemStack.EMPTY;
                } else if (getDyeColor(originalStack) != null) {
                    if (!moveItemStackTo(originalStack, DYE_START, DYE_END + 1, false))
                        return ItemStack.EMPTY;
                }
                slot.onQuickCraft(originalStack, newStack);
            }

            if (originalStack.isEmpty()) slot.setByPlayer(ItemStack.EMPTY);
            else slot.setChanged();
        }
        return newStack;
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        this.clearContainer(player, container);
    }

    @Override
    public boolean stillValid(Player player) {
        return container.stillValid(player);
    }

    public int getBaseMask() { return baseMaskData.get(); }
    public int getFadeMask() { return fadeMaskData.get(); }
    public Container getContainer() { return container; }
}