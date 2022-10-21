package net.rutrum.pyrotechnics;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemGroup;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class Pyrotechnics implements ModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger("pyrotechnics");

	public static final String MODID = "pyrotechnics";

	public static final FireworkAssemblyTableBlock FIREWORK_ASSEMBLY_TABLE = new FireworkAssemblyTableBlock(FabricBlockSettings.of(Material.METAL).strength(4.0f));

	// Defines the type of block entity (the type of the block)
	// Takes a "factory" which builds block entities from position and blockstate
	public static final BlockEntityType<FireworkAssemblyTableBlockEntity> FIREWORK_ASSEMBLY_TABLE_BLOCK_ENTITY_TYPE 
		= Registry.register(
			Registry.BLOCK_ENTITY_TYPE, 
			identifier("firework_assembly_table"),
			FabricBlockEntityTypeBuilder.create(
				// (pos, state) -> new FireworkAssemblyTableBlockEntity(pos, state), // infered by below
				FireworkAssemblyTableBlockEntity::new,
				FIREWORK_ASSEMBLY_TABLE).build(null));

	public static final ExtendedScreenHandlerType<FireworkAssemblyTableScreenHandler> FIREWORK_ASSEMBLY_TABLE_SCREEN_HANDLER_TYPE
        = Registry.register(
			Registry.SCREEN_HANDLER, 
			identifier("firework_assembly_table"),
			//(syncId, inventory) -> new FireworkAssemblyTableScreenHandler(syncId, inventory)); // kind of, but extended adds more things like the block entity
			new ExtendedScreenHandlerType<>(FireworkAssemblyTableScreenHandler::new));


	public static final RecipeType<FireworkAssemblyRecipe> FIREWORK_ASSEMBLY_RECIPE_TYPE
		= Registry.register(Registry.RECIPE_TYPE, new Identifier(MODID, "firework_assembly"), new RecipeType<FireworkAssemblyRecipe>(){
            public String toString() {
                return "firework_assembly";
            }
        });

	public static final RecipeSerializer<FireworkAssemblyRecipe> FIREWORK_ASSEMBLY_RECIPE_SERIALIZER
		= Registry.register(Registry.RECIPE_SERIALIZER, new Identifier(MODID, "firework_assembly"), new FireworkAssemblyRecipe.Serializer());

	@Override
	public void onInitialize() {
		LOGGER.debug("Hello Fabric world!");

		registerBlock("firework_assembly_table", FIREWORK_ASSEMBLY_TABLE);
	}

	private static Identifier identifier(String name) {
		return new Identifier(MODID, name);
	}

	private void registerBlock(String name, Block block) {
		Identifier id = new Identifier(MODID, name);
		Registry.register(Registry.BLOCK, id, block);
		Registry.register(Registry.ITEM, id, new BlockItem(block, new FabricItemSettings().group(ItemGroup.MISC)));
	}
}
