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
import net.rutrum.pyrotechnics.block.EffectBenchBlock;
import net.rutrum.pyrotechnics.block.EffectBenchBlockEntity;
import net.rutrum.pyrotechnics.screen.AssemblyBenchMenu;
import net.rutrum.pyrotechnics.screen.EffectBenchMenu;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Pyrotechnics implements ModInitializer {
	public static final String MOD_ID = "pyrotechnics";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	// ===== Assembly Bench =====

	public static final AssemblyBenchBlock ASSEMBLY_BENCH = Registry.register(
		BuiltInRegistries.BLOCK,
		Identifier.fromNamespaceAndPath(MOD_ID, "assembly_bench"),
		new AssemblyBenchBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.CRAFTING_TABLE).strength(2.5f))
	);

	public static final BlockItem ASSEMBLY_BENCH_ITEM = Registry.register(
		BuiltInRegistries.ITEM,
		Identifier.fromNamespaceAndPath(MOD_ID, "assembly_bench"),
		new BlockItem(ASSEMBLY_BENCH, new Item.Properties())
	);

	public static final BlockEntityType<AssemblyBenchBlockEntity> ASSEMBLY_BENCH_BLOCK_ENTITY = Registry.register(
		BuiltInRegistries.BLOCK_ENTITY_TYPE,
		Identifier.fromNamespaceAndPath(MOD_ID, "assembly_bench"),
		FabricBlockEntityTypeBuilder.create(AssemblyBenchBlockEntity::new, ASSEMBLY_BENCH).build()
	);

	public static final MenuType<AssemblyBenchMenu> ASSEMBLY_BENCH_MENU = Registry.register(
		BuiltInRegistries.MENU,
		Identifier.fromNamespaceAndPath(MOD_ID, "assembly_bench"),
		new MenuType<>(AssemblyBenchMenu::new, FeatureFlags.DEFAULT_FLAGS)
	);

	// ===== Effect Bench =====

	public static final EffectBenchBlock EFFECT_BENCH = Registry.register(
		BuiltInRegistries.BLOCK,
		Identifier.fromNamespaceAndPath(MOD_ID, "effect_bench"),
		new EffectBenchBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.CRAFTING_TABLE).strength(2.5f))
	);

	public static final BlockItem EFFECT_BENCH_ITEM = Registry.register(
		BuiltInRegistries.ITEM,
		Identifier.fromNamespaceAndPath(MOD_ID, "effect_bench"),
		new BlockItem(EFFECT_BENCH, new Item.Properties())
	);

	public static final BlockEntityType<EffectBenchBlockEntity> EFFECT_BENCH_BLOCK_ENTITY = Registry.register(
		BuiltInRegistries.BLOCK_ENTITY_TYPE,
		Identifier.fromNamespaceAndPath(MOD_ID, "effect_bench"),
		FabricBlockEntityTypeBuilder.create(EffectBenchBlockEntity::new, EFFECT_BENCH).build()
	);

	public static final MenuType<EffectBenchMenu> EFFECT_BENCH_MENU = Registry.register(
		BuiltInRegistries.MENU,
		Identifier.fromNamespaceAndPath(MOD_ID, "effect_bench"),
		new MenuType<>(EffectBenchMenu::new, FeatureFlags.DEFAULT_FLAGS)
	);

	@Override
	public void onInitialize() {
		LOGGER.info("Pyrotechnics initialized");
	}
}