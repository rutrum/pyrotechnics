package net.rutrum.pyrotechnics;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.gui.screen.ingame.HandledScreens;

public class PyrotechnicsClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        HandledScreens.register(Pyrotechnics.FIREWORK_ASSEMBLY_TABLE_SCREEN_HANDLER_TYPE, FireworkAssemblyTableScreen::new);
    }
}