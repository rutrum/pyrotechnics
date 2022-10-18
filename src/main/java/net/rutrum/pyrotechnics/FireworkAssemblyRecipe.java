package net.rutrum.pyrotechnics;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;


// referencing SmithingRecipe
public class FireworkAssemblyRecipe implements Recipe<Inventory> {

    public FireworkAssemblyRecipe() {
    }

    @Override
    public boolean matches(Inventory inventory, World world) {
        // 0 is paper
        return inventory.getStack(0).getItem() == Registry.ITEM.get(new Identifier("minecraft", "paper"));
    }

    @Override
    public ItemStack craft(Inventory var1) {
        return new ItemStack(Registry.ITEM.get(new Identifier("minecraft", "rocket")));
    }

    @Override
    public boolean fits(int var1, int var2) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public ItemStack getOutput() {
        return new ItemStack(Registry.ITEM.get(new Identifier("minecraft", "rocket")));
    }

    @Override
    public Identifier getId() {
        return new Identifier("pyrotechnics", "firework_assembly_paper");
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return null;
    }

    @Override
    public RecipeType<?> getType() {
        return null;
    }
    
}
