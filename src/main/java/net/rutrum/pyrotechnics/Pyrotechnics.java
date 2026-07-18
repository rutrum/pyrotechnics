package net.rutrum.pyrotechnics;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.Blocks;

import net.rutrum.pyrotechnics.config.PyrotechnicsConfig;
import net.rutrum.pyrotechnics.config.RemoveRecipesCondition;
import net.rutrum.pyrotechnics.block.AssemblyBenchBlock;
import net.rutrum.pyrotechnics.block.EffectBenchBlock;
import net.rutrum.pyrotechnics.block.ColorVatBlock;
import net.rutrum.pyrotechnics.screen.AssemblyBenchMenu;
import net.rutrum.pyrotechnics.screen.EffectBenchMenu;
import net.rutrum.pyrotechnics.screen.ColorVatMenu;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.function.Function;

public class Pyrotechnics implements ModInitializer {
	public static final String MOD_ID = "pyrotechnics";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	// ---- Helper methods (MC 26.2 registration pattern) ----

	private static <B extends Block> B registerBlock(String name, Function<BlockBehaviour.Properties, B> factory, BlockBehaviour.Properties properties) {
		ResourceKey<Block> key = ResourceKey.create(BuiltInRegistries.BLOCK.key(), Identifier.fromNamespaceAndPath(MOD_ID, name));
		B block = factory.apply(properties.setId(key));
		return Registry.register(BuiltInRegistries.BLOCK, key, block);
	}

	private static BlockItem registerBlockItem(String name, Block block, Item.Properties properties) {
		ResourceKey<Item> key = ResourceKey.create(BuiltInRegistries.ITEM.key(), Identifier.fromNamespaceAndPath(MOD_ID, name));
		return Registry.register(BuiltInRegistries.ITEM, key, new BlockItem(block, properties.setId(key)));
	}

	private static <T extends AbstractContainerMenu> MenuType<T> registerMenu(String name, MenuType.MenuSupplier<T> factory) {
		return Registry.register(
			BuiltInRegistries.MENU,
			Identifier.fromNamespaceAndPath(MOD_ID, name),
			new MenuType<>(factory, FeatureFlags.DEFAULT_FLAGS)
		);
	}

	// ===== Assembly Bench =====
	public static final AssemblyBenchBlock ASSEMBLY_BENCH = registerBlock(
		"assembly_bench",
		AssemblyBenchBlock::new,
		BlockBehaviour.Properties.ofFullCopy(Blocks.CRAFTING_TABLE).strength(2.5f)
	);
	public static final BlockItem ASSEMBLY_BENCH_ITEM = registerBlockItem(
		"assembly_bench",
		ASSEMBLY_BENCH,
		new Item.Properties()
	);
	public static final MenuType<AssemblyBenchMenu> ASSEMBLY_BENCH_MENU = registerMenu(
		"assembly_bench",
		AssemblyBenchMenu::new
	);

	// ===== Effect Bench =====
	public static final EffectBenchBlock EFFECT_BENCH = registerBlock(
		"effect_bench",
		EffectBenchBlock::new,
		BlockBehaviour.Properties.ofFullCopy(Blocks.CRAFTING_TABLE).strength(2.5f)
	);
	public static final BlockItem EFFECT_BENCH_ITEM = registerBlockItem(
		"effect_bench",
		EFFECT_BENCH,
		new Item.Properties()
	);
	public static final MenuType<EffectBenchMenu> EFFECT_BENCH_MENU = registerMenu(
		"effect_bench",
		EffectBenchMenu::new
	);

	// ===== Color Vat =====
	public static final ColorVatBlock COLOR_VAT = registerBlock(
		"color_vat",
		ColorVatBlock::new,
		BlockBehaviour.Properties.ofFullCopy(Blocks.CRAFTING_TABLE).strength(2.5f)
	);
	public static final BlockItem COLOR_VAT_ITEM = registerBlockItem(
		"color_vat",
		COLOR_VAT,
		new Item.Properties()
	);
	public static final MenuType<ColorVatMenu> COLOR_VAT_MENU = registerMenu(
		"color_vat",
		ColorVatMenu::new
	);

	@Override
	public void onInitialize() {
		PyrotechnicsConfig.getInstance();
		RemoveRecipesCondition.register();

		CreativeModeTabEvents.modifyOutputEvent(net.minecraft.world.item.CreativeModeTabs.FUNCTIONAL_BLOCKS)
			.register(output -> {
				output.accept(COLOR_VAT);
				output.accept(EFFECT_BENCH);
				output.accept(ASSEMBLY_BENCH);
			});

		LOGGER.info("Pyrotechnics initialized");
	}
}