package net.rutrum.pyrotechnics;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
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

	public static final FireworkCrafterBlock FIREWORK_CRAFTER = new FireworkCrafterBlock(FabricBlockSettings.of(Material.METAL).strength(4.0f));
    
    public static final ScreenHandlerType<FireworkCraftingScreenHandler> FIREWORK_CRAFTER_SCREEN_HANDLER 
		= ScreenHandlerRegistry.registerExtended(new Identifier("pyrotechnics", "firework_crafter"), FireworkCraftingScreenHandler::new);

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		LOGGER.info("Hello Fabric world!");
		Registry.register(Registry.BLOCK, new Identifier("pyrotechnics", "firework_crafter"), FIREWORK_CRAFTER);
		Registry.register(Registry.ITEM, new Identifier("pyrotechnics", "firework_crafter"), new BlockItem(FIREWORK_CRAFTER, new FabricItemSettings().group(ItemGroup.MISC)));

	}
}
