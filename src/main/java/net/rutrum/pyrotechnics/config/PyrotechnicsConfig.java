package net.rutrum.pyrotechnics.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;

public class PyrotechnicsConfig {
    public boolean removeVanillaRecipes = true;

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static PyrotechnicsConfig instance;

    public static PyrotechnicsConfig getInstance() {
        if (instance == null) {
            instance = load();
        }
        return instance;
    }

    private static PyrotechnicsConfig load() {
        Path configPath = FabricLoader.getInstance().getConfigDir().resolve("pyrotechnics.json");
        if (configPath.toFile().exists()) {
            try (FileReader reader = new FileReader(configPath.toFile())) {
                return GSON.fromJson(reader, PyrotechnicsConfig.class);
            } catch (IOException e) {
                // Fall through
            }
        }
        PyrotechnicsConfig config = new PyrotechnicsConfig();
        // Write default config
        try (FileWriter writer = new FileWriter(configPath.toFile())) {
            GSON.toJson(config, writer);
        } catch (IOException ignored) {}
        return config;
    }
}