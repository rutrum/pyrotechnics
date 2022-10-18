package net.rutrum.pyrotechnics;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class FireworkAssemblyTableScreenHandler extends ScreenHandler {

    protected final Inventory input = new SimpleInventory(3){
        @Override
        public void markDirty() {
            super.markDirty();
            FireworkAssemblyTableScreenHandler.this.onContentChanged(this);
        }
    };
    protected final Inventory result = new SimpleInventory(1);
    protected final ScreenHandlerContext context;

    public FireworkAssemblyTableScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(syncId, playerInventory, ScreenHandlerContext.EMPTY);
    }

    public FireworkAssemblyTableScreenHandler(int syncId, PlayerInventory playerInventory, ScreenHandlerContext context) {
        super(Pyrotechnics.FIREWORK_ASSEMBLY_TABLE_SCREEN_HANDLER_TYPE, syncId);

        this.context = context;

        input.onOpen(playerInventory.player);
        this.addSlot(new Slot(input, 0, 52, 10));
        this.addSlot(new Slot(input, 1, 52, 32));
        this.addSlot(new Slot(input, 2, 52, 54));
        this.addSlot(new Slot(result, 0, 110, 31) {
            @Override
            public void onTakeItem(PlayerEntity player, ItemStack stack) {
                FireworkAssemblyTableScreenHandler.this.onTakeOutput(player, stack);
            }

            @Override
            public boolean canInsert(ItemStack stack) {
                return false;
            }
        });

        addPlayerInventory(playerInventory);
        addPlayerHotbar(playerInventory);
    }

    private void addPlayerInventory(PlayerInventory playerInventory) {
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }
    }

    private void addPlayerHotbar(PlayerInventory playerInventory) {
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 142));
        }
    }

    // Called by `result` slot to remove items from `input` inventory
    private void onTakeOutput(PlayerEntity player, ItemStack stack) {
        stack.onCraft(player.world, player, stack.getCount());
        // this.result.unlockLastRecipe(player);
        this.decrementStack(0);
        this.decrementStack(1);
        //this.context.run((world, pos) -> world.syncWorldEvent(WorldEvents.SMITHING_TABLE_USED, (BlockPos)pos, 0));
    }
    
    // Copied from smithing screen, removes a single item from a slot number of `input`
    private void decrementStack(int slot) {
        ItemStack itemStack = this.input.getStack(slot);
        itemStack.decrement(1);
        this.input.setStack(slot, itemStack);
    }

    // Called when player closes the GUI.  TODO: DESTROYS INVENTORY
    @Override
    public void close(PlayerEntity player) {
        super.close(player);
        this.context.run((world, pos) -> this.dropInventory(player, this.input));
    }

    // Called by inventory (called `input`) to update the `result` stack
    @Override
    public void onContentChanged(Inventory inventory) {
        super.onContentChanged(inventory);
        if (inventory == this.input) {
            System.out.println(this.input.getStack(0));
            if (
                this.input.getStack(0).isOf(Registry.ITEM.get(new Identifier("minecraft", "paper")))
                && this.input.getStack(1).isOf(Registry.ITEM.get(new Identifier("minecraft", "gunpowder")))
            ) {
                this.result.setStack(0, new ItemStack(Registry.ITEM.get(new Identifier("minecraft", "firework_rocket"))));
            }
        }
    }

    // Boilerplate code for handling shift-clicking functionality.
    @Override
    public ItemStack transferSlot(PlayerEntity player, int invSlot) {
        ItemStack newStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(invSlot);
        if (slot != null && slot.hasStack()) {
            ItemStack originalStack = slot.getStack();
            newStack = originalStack.copy();
            if (invSlot < this.input.size()) {
                if (!this.insertItem(originalStack, this.input.size(), this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.insertItem(originalStack, 0, this.input.size(), false)) {
                return ItemStack.EMPTY;
            }
 
            if (originalStack.isEmpty()) {
                slot.setStack(ItemStack.EMPTY);
            } else {
                slot.markDirty();
            }
        }
 
        return newStack;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return this.input.canPlayerUse(player);
    }
}