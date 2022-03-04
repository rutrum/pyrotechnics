package net.rutrum.pyrotechnics;

import com.ibm.icu.text.CaseMap.Upper;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.CraftingResultInventory;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeMatcher;
import net.minecraft.recipe.book.RecipeBookCategory;
import net.minecraft.screen.AbstractRecipeScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.CraftingResultSlot;
import net.minecraft.screen.slot.Slot;
import net.rutrum.pyrotechnics.Pyrotechnics;

public class FireworkCraftingScreenHandler 
extends ScreenHandler {
    private final ScreenHandlerContext context;

    public FireworkCraftingScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(syncId, playerInventory, ScreenHandlerContext.EMPTY);
    }

    public FireworkCraftingScreenHandler(int syncId, PlayerInventory playerInventory, ScreenHandlerContext context) {
        super(Pyrotechnics.FIREWORK_CRAFTER_SCREEN_HANDLER, syncId);
        this.context = null;
    }

    @Override
    public boolean canInsertIntoSlot(int index) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void clearCraftingSlots() {
        this.input.clear();
        this.result.clear();
    }

    @Override
    public RecipeBookCategory getCategory() {
        return RecipeBookCategory.CRAFTING;
    }

    @Override
    public int getCraftingHeight() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int getCraftingResultSlotIndex() {
        return 0;
    }

    @Override
    public int getCraftingSlotCount() {
        return 4;
    }

    @Override
    public int getCraftingWidth() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public boolean matches(Recipe<? super CraftingInventory> recipe) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void populateRecipeFinder(RecipeMatcher finder) {
        // TODO Auto-generated method stub
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return FireworkCraftingScreenHandler.canUse(this.context, player, Pyrotechnics.FIREWORK_CRAFTER);
    }
}
