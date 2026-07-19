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
import net.minecraft.world.item.component.Fireworks;
import net.minecraft.core.component.DataComponents;

import net.rutrum.pyrotechnics.Pyrotechnics;

import java.util.ArrayList;
import java.util.List;

public class AssemblyBenchMenu extends AbstractContainerMenu {

    private static final int PAPER_SLOT = 0;
    private static final int GUNPOWDER_SLOT = 1;
    private static final int STAR_SLOT_START = 2;
    private static final int STAR_SLOT_END = 9; // inclusive, 8 slots (indices 2-9)
    private static final int RESULT_SLOT = 10;
    private static final int CONTAINER_SIZE = 11;

    private final Container container;
    private final Container resultContainer = new SimpleContainer(1);

    // Client constructor — server syncs inventory via slots
    public AssemblyBenchMenu(int syncId, Inventory playerInventory) {
        this(syncId, playerInventory, new SimpleContainer(CONTAINER_SIZE));
    }

    // Server constructor — takes the real block entity's inventory
    public AssemblyBenchMenu(int syncId, Inventory playerInventory, Container container) {
        super(Pyrotechnics.ASSEMBLY_BENCH_MENU, syncId);
        this.container = container;
        container.startOpen(playerInventory.player);

        // Slot 0: Paper
        this.addSlot(new Slot(container, PAPER_SLOT, 44, 35) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return stack.is(Items.PAPER);
            }
        });

        // Slot 1: Gunpowder
        this.addSlot(new Slot(container, GUNPOWDER_SLOT, 62, 35) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return stack.is(Items.GUNPOWDER);
            }
        });

        // Slots 2-9: Firework Stars (8 slots)
        for (int i = 0; i < 8; i++) {
            this.addSlot(new Slot(container, STAR_SLOT_START + i, 98 + (i % 4) * 18, 17 + (i / 4) * 18) {
                @Override
                public boolean mayPlace(ItemStack stack) {
                    return stack.is(Items.FIREWORK_STAR);
                }
            });
        }

        // Slot 10: Result
        this.addSlot(new Slot(container, RESULT_SLOT, 152, 35) {
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

        // Player hotbar
        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(playerInventory, col, 8 + col * 18, 142));
        }
    }

    private void onTakeOutput(Player player, ItemStack rocketStack) {
        // Consume paper
        ItemStack paperStack = container.getItem(PAPER_SLOT);
        paperStack.shrink(1);

        // Consume gunpowder (up to 3)
        ItemStack gunpowderStack = container.getItem(GUNPOWDER_SLOT);
        int gunpowderCount = gunpowderStack.getCount();
        int toConsume = Math.min(gunpowderCount, 3);
        gunpowderStack.shrink(toConsume);

        // Consume 1 from each star slot that's used
        for (int i = STAR_SLOT_START; i <= STAR_SLOT_END; i++) {
            ItemStack starStack = container.getItem(i);
            if (!starStack.isEmpty()) {
                starStack.shrink(1);
            }
        }

        container.setChanged();
    }

    /**
     * Builds the rocket item based on the current inputs.
     * Called whenever the input inventory changes.
     */
    private ItemStack buildRocket() {
        // Must have paper
        ItemStack paperStack = container.getItem(PAPER_SLOT);
        if (paperStack.isEmpty()) return ItemStack.EMPTY;

        // Must have at least 1 gunpowder
        ItemStack gunpowderStack = container.getItem(GUNPOWDER_SLOT);
        int gunpowderCount = gunpowderStack.getCount();
        if (gunpowderCount < 1) return ItemStack.EMPTY;

        // Read firework stars
        List<FireworkExplosion> explosions = new ArrayList<>();
        for (int i = STAR_SLOT_START; i <= STAR_SLOT_END; i++) {
            ItemStack starStack = container.getItem(i);
            if (!starStack.isEmpty()) {
                FireworkExplosion explosion = starStack.get(DataComponents.FIREWORK_EXPLOSION);
                if (explosion != null) {
                    explosions.add(explosion);
                }
            }
        }

        // Determine flight duration (1-3 based on gunpowder count)
        int flightDuration = Math.min(gunpowderCount, 3);

        // Create the rocket item
        ItemStack rocket = new ItemStack(Items.FIREWORK_ROCKET, 3);
        rocket.set(DataComponents.FIREWORKS, new Fireworks(flightDuration, explosions));

        return rocket;
    }

    @Override
    public void slotsChanged(Container inventory) {
        super.slotsChanged(inventory);
        if (inventory == this.container) {
            ItemStack result = buildRocket();
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
                // Result slot — move to player inventory
                if (!this.moveItemStackTo(originalStack, 11, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
                slot.onQuickCraft(originalStack, newStack);
            } else if (slotIndex < CONTAINER_SIZE) {
                // Container slots — move to player inventory
                if (!this.moveItemStackTo(originalStack, 11, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                // Player inventory — move to appropriate container slot
                if (originalStack.is(Items.PAPER)) {
                    if (!this.moveItemStackTo(originalStack, PAPER_SLOT, PAPER_SLOT + 1, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (originalStack.is(Items.GUNPOWDER)) {
                    if (!this.moveItemStackTo(originalStack, GUNPOWDER_SLOT, GUNPOWDER_SLOT + 1, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (originalStack.is(Items.FIREWORK_STAR)) {
                    if (!this.moveItemStackTo(originalStack, STAR_SLOT_START, STAR_SLOT_END + 1, false)) {
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