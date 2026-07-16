package net.rutrum.pyrotechnics;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.Blocks;

import net.rutrum.pyrotechnics.block.AssemblyBenchBlock;
import net.rutrum.pyrotechnics.block.AssemblyBenchBlockEntity;
import net.rutrum.pyrotechnics.screen.AssemblyBenchMenu;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Pyrotechnics implements ModInitializer {
	public static final String MOD_ID = "pyrotechnics";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	// Blocks
	public static final AssemblyBenchBlock ASSEMBLY_BENCH = Registry.register(
		BuiltInRegistries.BLOCK,
		Identifier.fromNamespaceAndPath(MOD_ID, "assembly_bench"),
		new AssemblyBenchBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.CRAFTING_TABLE).strength(2.5f))
	);

	// Block Items
	public static final BlockItem ASSEMBLY_BENCH_ITEM = Registry.register(
		BuiltInRegistries.ITEM,
		Identifier.fromNamespaceAndPath(MOD_ID, "assembly_bench"),
		new BlockItem(ASSEMBLY_BENCH, new Item.Properties())
	);

	// Block Entities
	public static final BlockEntityType<AssemblyBenchBlockEntity> ASSEMBLY_BENCH_BLOCK_ENTITY = Registry.register(
		BuiltInRegistries.BLOCK_ENTITY_TYPE,
		Identifier.fromNamespaceAndPath(MOD_ID, "assembly_bench"),
		FabricBlockEntityTypeBuilder.create(AssemblyBenchBlockEntity::new, ASSEMBLY_BENCH).build()
	);

	// Screen Handlers (Menus)
	public static final MenuType<AssemblyBenchMenu> ASSEMBLY_BENCH_MENU = Registry.register(
		BuiltInRegistries.MENU,
		Identifier.fromNamespaceAndPath(MOD_ID, "assembly_bench"),
		new MenuType<>(AssemblyBenchMenu::new, FeatureFlags.DEFAULT_FLAGS)
	);

	@Override
	public void onInitialize() {
		LOGGER.info("Pyrotechnics initialized");
	}
}