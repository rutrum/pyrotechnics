package net.rutrum.pyrotechnics;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemGroup;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Pyrotechnics implements ModInitializer {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger("pyrotechnics");

	public static final String MODID = "pyrotechnics";

	public static final FireworkAssemblyTableBlock FIREWORK_ASSEMBLY_TABLE = new FireworkAssemblyTableBlock(FabricBlockSettings.of(Material.METAL).strength(4.0f));

	public static final ScreenHandlerType<FireworkAssemblyTableScreenHandler> FIREWORK_ASSEMBLY_TABLE_SCREEN_HANDLER_TYPE
        = Registry.register(Registry.SCREEN_HANDLER, new Identifier(MODID, "firework_assembly_table"), new ScreenHandlerType<>(FireworkAssemblyTableScreenHandler::new));

	@Override
	public void onInitialize() {
		LOGGER.debug("Hello Fabric world!");

		registerBlock("firework_assembly_table", FIREWORK_ASSEMBLY_TABLE);
	}

	private void registerBlock(String name, Block block) {
		Identifier id = new Identifier(MODID, name);
		Registry.register(Registry.BLOCK, id, block);
		Registry.register(Registry.ITEM, id, new BlockItem(block, new FabricItemSettings().group(ItemGroup.MISC)));
	}
}
