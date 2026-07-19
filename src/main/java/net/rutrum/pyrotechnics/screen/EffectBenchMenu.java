package net.rutrum.pyrotechnics.screen;

import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.FireworkExplosion;
import net.minecraft.core.component.DataComponents;

import net.rutrum.pyrotechnics.Pyrotechnics;

import it.unimi.dsi.fastutil.ints.IntList;

public class EffectBenchMenu extends AbstractContainerMenu {

    private static final int STAR_SLOT = 0;
    private static final int SHAPE_SLOT = 1;
    private static final int DIAMOND_SLOT = 2;
    private static final int GLOWSTONE_SLOT = 3;
    private static final int RESULT_SLOT = 4;
    private static final int CONTAINER_SIZE = 5;

    private final Container container;

    // Client constructor
    public EffectBenchMenu(int syncId, Inventory playerInventory) {
        this(syncId, playerInventory, new SimpleContainer(CONTAINER_SIZE));
    }

    // Server constructor
    public EffectBenchMenu(int syncId, Inventory playerInventory, Container container) {
        super(Pyrotechnics.EFFECT_BENCH_MENU, syncId);
        this.container = container;
        container.startOpen(playerInventory.player);

        // Slot 0: Firework star (input)
        this.addSlot(new Slot(container, STAR_SLOT, 44, 35) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return stack.is(Items.FIREWORK_STAR);
            }
        });

        // Slot 1: Shape modifier (feather, gold nugget, fire charge, mob head)
        this.addSlot(new Slot(container, SHAPE_SLOT, 26, 57) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return stack.is(Items.FEATHER)
                    || stack.is(Items.GOLD_NUGGET)
                    || stack.is(Items.FIRE_CHARGE)
                    || stack.is(Items.WITHER_SKELETON_SKULL)
                    || stack.is(Items.SKELETON_SKULL)
                    || stack.is(Items.ZOMBIE_HEAD)
                    || stack.is(Items.PLAYER_HEAD)
                    || stack.is(Items.CREEPER_HEAD)
                    || stack.is(Items.PIGLIN_HEAD)
                    || stack.is(Items.DRAGON_HEAD);
            }
        });

        // Slot 2: Diamond (trail)
        this.addSlot(new Slot(container, DIAMOND_SLOT, 62, 57) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return stack.is(Items.DIAMOND);
            }
        });

        // Slot 3: Glowstone dust (twinkle)
        this.addSlot(new Slot(container, GLOWSTONE_SLOT, 80, 57) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return stack.is(Items.GLOWSTONE_DUST);
            }
        });

        // Slot 4: Result
        this.addSlot(new Slot(container, RESULT_SLOT, 134, 35) {
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
                this.addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
            }
        }
        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(playerInventory, col, 8 + col * 18, 142));
        }
    }

    private void onTakeOutput(Player player, ItemStack resultStack) {
        // Consume the input star
        container.getItem(STAR_SLOT).shrink(1);

        // Consume shape modifier if present
        if (!container.getItem(SHAPE_SLOT).isEmpty()) {
            container.getItem(SHAPE_SLOT).shrink(1);
        }

        // Consume diamond if present
        if (!container.getItem(DIAMOND_SLOT).isEmpty()) {
            container.getItem(DIAMOND_SLOT).shrink(1);
        }

        // Consume glowstone if present
        if (!container.getItem(GLOWSTONE_SLOT).isEmpty()) {
            container.getItem(GLOWSTONE_SLOT).shrink(1);
        }

        container.setChanged();
    }

    private ItemStack buildStar() {
        ItemStack starStack = container.getItem(STAR_SLOT);
        if (starStack.isEmpty()) return ItemStack.EMPTY;

        FireworkExplosion explosion = starStack.get(DataComponents.FIREWORK_EXPLOSION);
        if (explosion == null) return ItemStack.EMPTY;

        // Read existing properties
        FireworkExplosion.Shape shape = explosion.shape();
        boolean hasTrail = explosion.hasTrail();
        boolean hasTwinkle = explosion.hasTwinkle();
        IntList colors = explosion.colors();
        IntList fadeColors = explosion.fadeColors();

        // Check for shape modifier
        ItemStack shapeStack = container.getItem(SHAPE_SLOT);
        if (!shapeStack.isEmpty()) {
            if (shapeStack.is(Items.FEATHER)) {
                shape = FireworkExplosion.Shape.BURST;
            } else if (shapeStack.is(Items.GOLD_NUGGET)) {
                shape = FireworkExplosion.Shape.STAR;
            } else if (shapeStack.is(Items.FIRE_CHARGE)) {
                shape = FireworkExplosion.Shape.LARGE_BALL;
            } else {
                // Any mob head
                shape = FireworkExplosion.Shape.CREEPER;
            }
        }

        // Check for diamond (trail)
        if (!container.getItem(DIAMOND_SLOT).isEmpty()) {
            hasTrail = true;
        }

        // Check for glowstone (twinkle)
        if (!container.getItem(GLOWSTONE_SLOT).isEmpty()) {
            hasTwinkle = true;
        }

        // Build the new explosion
        FireworkExplosion modified = new FireworkExplosion(shape, colors, fadeColors, hasTrail, hasTwinkle);

        // Create output star and set the component
        ItemStack result = new ItemStack(Items.FIREWORK_STAR, 1);
        result.set(DataComponents.FIREWORK_EXPLOSION, modified);

        return result;
    }

    @Override
    public void slotsChanged(Container inventory) {
        super.slotsChanged(inventory);
        if (inventory == this.container) {
            ItemStack result = buildStar();
            container.setItem(RESULT_SLOT, result);
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
                if (!this.moveItemStackTo(originalStack, CONTAINER_SIZE, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
                slot.onQuickCraft(originalStack, newStack);
            } else if (slotIndex < CONTAINER_SIZE) {
                if (!this.moveItemStackTo(originalStack, CONTAINER_SIZE, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                if (originalStack.is(Items.FIREWORK_STAR)) {
                    if (!this.moveItemStackTo(originalStack, STAR_SLOT, STAR_SLOT + 1, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (originalStack.is(Items.FEATHER) || originalStack.is(Items.GOLD_NUGGET)
                    || originalStack.is(Items.FIRE_CHARGE)
                    || originalStack.is(Items.WITHER_SKELETON_SKULL) || originalStack.is(Items.SKELETON_SKULL)
                    || originalStack.is(Items.ZOMBIE_HEAD) || originalStack.is(Items.PLAYER_HEAD)
                    || originalStack.is(Items.CREEPER_HEAD) || originalStack.is(Items.PIGLIN_HEAD)
                    || originalStack.is(Items.DRAGON_HEAD)) {
                    if (!this.moveItemStackTo(originalStack, SHAPE_SLOT, SHAPE_SLOT + 1, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (originalStack.is(Items.DIAMOND)) {
                    if (!this.moveItemStackTo(originalStack, DIAMOND_SLOT, DIAMOND_SLOT + 1, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (originalStack.is(Items.GLOWSTONE_DUST)) {
                    if (!this.moveItemStackTo(originalStack, GLOWSTONE_SLOT, GLOWSTONE_SLOT + 1, false)) {
                        return ItemStack.EMPTY;
                    }
                } else {
                    return ItemStack.EMPTY;
                }
                slot.onQuickCraft(originalStack, newStack);
            }

            if (originalStack.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
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

    public Container getContainer() {
        return container;
    }
}