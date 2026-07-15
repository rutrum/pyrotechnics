package net.rutrum.pyrotechnics;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Pyrotechnics implements ModInitializer {
	public static final String MOD_ID = "pyrotechnics";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		LOGGER.info("Pyrotechnics initialized");
	}
}
