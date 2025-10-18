package ru.ntwz.jasds.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;
import ru.ntwz.jasds.JustAnotherSurvivalDebugStick;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ConfigManager {
    private static Path configsDirectory;
    private static File configFile;
    private static JASDSConfig config;

    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    public static void loadConfig() {
        getConfigsDirectory();

        configFile = new File(configsDirectory.toFile(), "config.json");

        if (!configFile.exists()) {
            config = new JASDSConfig();
            saveConfig();
            JustAnotherSurvivalDebugStick.LOGGER.info("Created default config.json");
        } else {
            try (FileReader reader = new FileReader(configFile)) {
                config = GSON.fromJson(reader, JASDSConfig.class);
                if (config == null) {
                    config = new JASDSConfig();
                    JustAnotherSurvivalDebugStick.LOGGER.warn("config.json is empty - creating default config");
                }
            } catch (IOException e) {
                JustAnotherSurvivalDebugStick.LOGGER.error("Failed to read config.json", e);
                config = new JASDSConfig();
            }
        }
    }

    public static void saveConfig() {
        try (FileWriter writer = new FileWriter(configFile)) {
            GSON.toJson(config, writer);
        } catch (IOException e) {
            JustAnotherSurvivalDebugStick.LOGGER.error("Failed to save config", e);
        }
    }

    public static JASDSConfig getConfig() {
        if (config == null) {
            loadConfig();
        }
        return config;
    }

    private static void getConfigsDirectory() {
        if (configsDirectory == null) {
            Path minecraftDir = FabricLoader.getInstance().getConfigDir();
            configsDirectory = minecraftDir.resolve("jasds");

            try {
                Files.createDirectories(configsDirectory);
            } catch (IOException e) {
                JustAnotherSurvivalDebugStick.LOGGER.error("Failed to load JASDS configs folder", e);
            }
        }
    }
}