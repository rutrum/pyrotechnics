package net.rutrum.pyrotechnics;

import java.util.List;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.slot.Slot;
import net.minecraft.world.World;

public class FireworkAssemblyTableScreenHandler extends ScreenHandler {

    final Inventory input = new SimpleInventory(3){
        @Override
        public void markDirty() {
            super.markDirty();
            FireworkAssemblyTableScreenHandler.this.onContentChanged(this);
        }
    };
    final Inventory result = new SimpleInventory(1);
    private final ScreenHandlerContext context;
    private final World world;

    public FireworkAssemblyTableScreenHandler(int syncId, PlayerInventory playerInventory, PacketByteBuf buf) {
        this(syncId,
            playerInventory, 
            playerInventory.player.getWorld().getBlockEntity(buf.readBlockPos()), // block entity
            ScreenHandlerContext.EMPTY);
    }

    public FireworkAssemblyTableScreenHandler(int syncId, PlayerInventory playerInventory, BlockEntity entity, ScreenHandlerContext context) {
        super(Pyrotechnics.FIREWORK_ASSEMBLY_TABLE_SCREEN_HANDLER_TYPE, syncId);

        this.context = context;
        this.world = playerInventory.player.world;

        input.onOpen(playerInventory.player);
        this.addSlot(new Slot(input, 0, 52, 17){
            @Override
            public boolean canInsert(ItemStack stack) {
                return stack.isOf(Items.PAPER);
            }
        });
        this.addSlot(new Slot(input, 1, 52, 39){
            @Override
            public boolean canInsert(ItemStack stack) {
                return stack.isOf(Items.GUNPOWDER);
            }
        });
        this.addSlot(new Slot(input, 2, 52, 61){
            @Override
            public boolean canInsert(ItemStack stack) {
                return stack.isOf(Items.FIREWORK_STAR);
            }
        });
        this.addSlot(new Slot(result, 0, 110, 39) {
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
                this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 91 + i * 18));
            }
        }
    }

    private void addPlayerHotbar(PlayerInventory playerInventory) {
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 149));
        }
    } 
    
    // TODO: Do this instead of forcing stacks to be explicit items, this will help with recipes
    public boolean canInsertIntoSlot(ItemStack stack, Slot slot) {
        return true;
    }

    // Called by `result` slot to remove items from `input` inventory
    private void onTakeOutput(PlayerEntity player, ItemStack stack) {
        stack.onCraft(player.world, player, stack.getCount());
        this.decrementStack(0);
        int totalGunpowder = this.input.getStack(1).getCount();
        int duration = totalGunpowder > 3 ? 3 : totalGunpowder; 

        for (int i = 0; i < duration; i++) {
            this.decrementStack(1);
        }
        this.decrementStack(2);
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
            List<FireworkAssemblyRecipe> list = this.world.getRecipeManager()
                .getAllMatches(Pyrotechnics.FIREWORK_ASSEMBLY_RECIPE_TYPE, this.input, this.world);

            if (list.isEmpty()) {
                this.result.setStack(0, ItemStack.EMPTY);
            } else {
                ItemStack itemStack = list.get(0).craft(this.input);
                this.result.setStack(0, itemStack);
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