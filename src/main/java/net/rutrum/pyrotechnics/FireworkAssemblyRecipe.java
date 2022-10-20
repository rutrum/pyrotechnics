package net.rutrum.pyrotechnics;

import com.google.gson.JsonObject;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
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
    final Ingredient star;
    final ItemStack result;

    public FireworkAssemblyRecipe(Identifier id, Ingredient paper, Ingredient gunpowder, Ingredient star, ItemStack result) {
        this.id = id;
        this.paper = paper;
        this.gunpowder = gunpowder;
        this.star = star;
        this.result = result;
    }

    @Override
    public boolean matches(Inventory inventory, World world) {
        boolean match_paper = this.paper.test(inventory.getStack(0));
        System.out.println("paper " + match_paper);
        boolean match_gunpowder = this.gunpowder.test(inventory.getStack(1));
        System.out.println("gunpowder " + match_gunpowder);
        // boolean match_star = this.star.test(inventory.getStack(2));
        boolean match_star = Ingredient.ofItems(Items.FIREWORK_STAR).test(inventory.getStack(2));
        System.out.println("star " + match_star);
        return match_gunpowder && match_paper && match_star;
    }

    @Override
    public ItemStack craft(Inventory inventory) {
        ItemStack fireworks = this.result.copy();
        NbtCompound fireworksNbt = fireworks.getOrCreateSubNbt("Fireworks");
        NbtList nbtList = new NbtList();

        NbtCompound explosion = inventory.getStack(2).getSubNbt("Explosion");
        if (explosion != null) nbtList.add(explosion);

        int totalGunpowder = inventory.getStack(1).getCount();
        int duration = totalGunpowder > 3 ? 3 : totalGunpowder; 

        if (!nbtList.isEmpty()) {
            fireworksNbt.put("Explosions", nbtList);
        }
        fireworksNbt.putByte("Flight", (byte)duration);
        return fireworks;
    }

    /*
    @Override
    public ItemStack craft(CraftingInventory craftingInventory) {
        ItemStack fireworks = new ItemStack(Items.FIREWORK_ROCKET, 3);
        NbtCompound fireworks_nbt = fireworks.getOrCreateSubNbt("Fireworks");
        NbtList nbtList = new NbtList();
        int i = 0;
        for (int j = 0; j < craftingInventory.size(); ++j) {
            NbtCompound nbtCompound2;
            ItemStack current_stack = craftingInventory.getStack(j);
            if (current_stack.isEmpty()) continue;
            if (DURATION_MODIFIER.test(current_stack)) {
                ++i;
                continue;
            }
            if (!FIREWORK_STAR.test(current_stack) || (nbtCompound2 = current_stack.getSubNbt("Explosion")) == null) continue;
            nbtList.add(nbtCompound2);
        }
        fireworks_nbt.putByte("Flight", (byte)i);
        if (!nbtList.isEmpty()) {
            fireworks_nbt.put("Explosions", nbtList);
        }
        return fireworks;
    }
    */

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
            Ingredient paper = Ingredient.fromJson(JsonHelper.getObject(jsonObject, "paper"));
            Ingredient gunpowder = Ingredient.fromJson(JsonHelper.getObject(jsonObject, "gunpowder"));
            Ingredient star = Ingredient.fromJson(JsonHelper.getObject(jsonObject, "gunpowder"));
            ItemStack fireworks = ShapedRecipe.outputFromJson(JsonHelper.getObject(jsonObject, "result"));
            return new FireworkAssemblyRecipe(identifier, paper, gunpowder, star, fireworks);
        }

        @Override
        public FireworkAssemblyRecipe read(Identifier identifier, PacketByteBuf packetByteBuf) {
            Ingredient paper = Ingredient.fromPacket(packetByteBuf);
            Ingredient gunpowder = Ingredient.fromPacket(packetByteBuf);
            Ingredient star = Ingredient.fromPacket(packetByteBuf);
            ItemStack fireworks = packetByteBuf.readItemStack();
            return new FireworkAssemblyRecipe(identifier, paper, gunpowder, star, fireworks);
        }

        @Override
        public void write(PacketByteBuf packetByteBuf, FireworkAssemblyRecipe recipe) {
            recipe.paper.write(packetByteBuf);
            recipe.gunpowder.write(packetByteBuf);
            packetByteBuf.writeItemStack(recipe.result);
        }
    }
}
