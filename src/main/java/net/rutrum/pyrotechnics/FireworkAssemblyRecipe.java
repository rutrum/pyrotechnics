package net.rutrum.pyrotechnics;

import com.google.gson.JsonObject;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.ShapedRecipe;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.world.World;


// referencing SmithingRecipe
public class FireworkAssemblyRecipe implements Recipe<Inventory> {

    private final Identifier id;
    final Ingredient paper;
    final Ingredient gunpowder;
    //final Ingredient firework_star;
    final ItemStack result;

    public FireworkAssemblyRecipe(Identifier id, Ingredient paper, Ingredient gunpowder, ItemStack result) {
        this.id = id;
        this.paper = paper;
        this.gunpowder = gunpowder;
        //this.firework_star = firework_star;
        this.result = result;
    }

    @Override
    public boolean matches(Inventory inventory, World world) {
        return this.paper.test(inventory.getStack(0)) && this.gunpowder.test(inventory.getStack(1));
    }

    @Override
    public ItemStack craft(Inventory var1) {
        ItemStack itemStack = this.result.copy();
        return itemStack;
        //return new ItemStack(Registry.ITEM.get(new Identifier("minecraft", "rocket")));
    }

    // docs say only relevant to recipe book
    @Override
    public boolean fits(int width, int height) {
        return width * height >= 2;
    }

    @Override
    public ItemStack getOutput() {
        return this.result;
    }

    @Override
    public Identifier getId() {
        return this.id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return Pyrotechnics.FIREWORK_ASSEMBLY_RECIPE_SERIALIZER;
    }

    @Override
    public RecipeType<?> getType() {
        return Pyrotechnics.FIREWORK_ASSEMBLY_RECIPE_TYPE;
    }
    
    public static class Serializer
    implements RecipeSerializer<FireworkAssemblyRecipe> {
        @Override
        public FireworkAssemblyRecipe read(Identifier identifier, JsonObject jsonObject) {
            Ingredient ingredient = Ingredient.fromJson(JsonHelper.getObject(jsonObject, "paper"));
            Ingredient ingredient2 = Ingredient.fromJson(JsonHelper.getObject(jsonObject, "gunpowder"));
            ItemStack itemStack = ShapedRecipe.outputFromJson(JsonHelper.getObject(jsonObject, "result"));
            return new FireworkAssemblyRecipe(identifier, ingredient, ingredient2, itemStack);
        }

        @Override
        public FireworkAssemblyRecipe read(Identifier identifier, PacketByteBuf packetByteBuf) {
            Ingredient ingredient = Ingredient.fromPacket(packetByteBuf);
            Ingredient ingredient2 = Ingredient.fromPacket(packetByteBuf);
            ItemStack itemStack = packetByteBuf.readItemStack();
            return new FireworkAssemblyRecipe(identifier, ingredient, ingredient2, itemStack);
        }

        @Override
        public void write(PacketByteBuf packetByteBuf, FireworkAssemblyRecipe recipe) {
            recipe.paper.write(packetByteBuf);
            recipe.gunpowder.write(packetByteBuf);
            packetByteBuf.writeItemStack(recipe.result);
        }
    }
}
