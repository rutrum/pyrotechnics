package net.rutrum.pyrotechnics.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import net.rutrum.pyrotechnics.Pyrotechnics;
import net.rutrum.pyrotechnics.screen.ColorVatMenu;

public class ColorVatBlockEntity extends BlockEntity implements Container, MenuProvider {

    // 16 dye slots, 1 gunpowder, 1 result
    private final NonNullList<ItemStack> items = NonNullList.withSize(18, ItemStack.EMPTY);

    // Bitmasks: bit 0 = slot 0, etc. 1 = selected for that category
    private int baseMask = 0;
    private int fadeMask = 0;

    public ColorVatBlockEntity(BlockPos pos, BlockState state) {
        super(Pyrotechnics.COLOR_VAT_BLOCK_ENTITY, pos, state);
    }

    @Override
    public int getContainerSize() {
        return items.size();
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack stack : items) {
            if (!stack.isEmpty()) return false;
        }
        return true;
    }

    @Override
    public ItemStack getItem(int slot) {
        return items.get(slot);
    }

    @Override
    public ItemStack removeItem(int slot, int count) {
        ItemStack result = ContainerHelper.removeItem(items, slot, count);
        if (!result.isEmpty()) setChanged();
        return result;
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        return ContainerHelper.takeItem(items, slot);
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        items.set(slot, stack);
        stack.limitSize(getMaxStackSize(stack));
        setChanged();
    }

    @Override
    public void setChanged() {
        super.setChanged();
    }

    @Override
    public boolean stillValid(Player player) {
        return Container.stillValidBlockEntity(this, player);
    }

    @Override
    public void clearContent() {
        items.clear();
        baseMask = 0;
        fadeMask = 0;
    }

    public int getBaseMask() { return baseMask; }
    public int getFadeMask() { return fadeMask; }

    public void setBaseMask(int mask) { this.baseMask = mask; setChanged(); }
    public void setFadeMask(int mask) { this.fadeMask = mask; setChanged(); }

    /** Toggle a dye slot as base color. Returns the new mask. */
    public int toggleBase(int slotIndex) {
        baseMask ^= (1 << slotIndex);
        setChanged();
        return baseMask;
    }

    /** Toggle a dye slot as fade color. Returns the new mask. */
    public int toggleFade(int slotIndex) {
        fadeMask ^= (1 << slotIndex);
        setChanged();
        return fadeMask;
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        ContainerHelper.loadAllItems(input, items);
        baseMask = input.getIntOr("BaseMask", 0);
        fadeMask = input.getIntOr("FadeMask", 0);
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        ContainerHelper.saveAllItems(output, items);
        output.putInt("BaseMask", baseMask);
        output.putInt("FadeMask", fadeMask);
        super.saveAdditional(output);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.pyrotechnics.color_vat");
    }

    @Override
    public AbstractContainerMenu createMenu(int syncId, Inventory playerInventory, Player player) {
        return new ColorVatMenu(syncId, playerInventory, this);
    }
}