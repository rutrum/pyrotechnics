package net.rutrum.pyrotechnics.client;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.gui.screens.MenuScreens;

import net.rutrum.pyrotechnics.Pyrotechnics;
import net.rutrum.pyrotechnics.client.screen.AssemblyBenchScreen;

public class PyrotechnicsClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		MenuScreens.register(Pyrotechnics.ASSEMBLY_BENCH_MENU, AssemblyBenchScreen::new);
	}
}
