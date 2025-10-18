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
    private static File blacklistFile;
    private static File whitelistFile;
    private static JASDSConfig config;
    private static WhitelistConfig whitelistConfig;
    private static BlacklistConfig blacklistConfig;

    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    public static void loadConfig() {
        getConfigsDirectory();

        configFile = new File(configsDirectory.toFile(), "config.json");
        blacklistFile = new File(configsDirectory.toFile(), "blacklist.json");
        whitelistFile = new File(configsDirectory.toFile(), "whitelist.json");

        if (!configFile.exists()) {
            config = new JASDSConfig();
            saveConfig();
            JustAnotherSurvivalDebugStick.LOGGER.info("Created default config.json");
        } else {
            try (FileReader reader = new FileReader(configFile)) {
                config = GSON.fromJson(reader, JASDSConfig.class);
                if (config == null) {
                    config = new JASDSConfig();
                    JustAnotherSurvivalDebugStick.LOGGER.warn("config.json is empty - created default config");
                }
            } catch (IOException e) {
                JustAnotherSurvivalDebugStick.LOGGER.error("Failed to read config.json", e);
                config = new JASDSConfig();
            }
            saveConfig();
        }

        if (!blacklistFile.exists()) {
            blacklistConfig = new BlacklistConfig();
            saveBlacklist();
            JustAnotherSurvivalDebugStick.LOGGER.info("Created default blacklist.json");
        } else {
            try (FileReader reader = new FileReader(blacklistFile)) {
                blacklistConfig = GSON.fromJson(reader, BlacklistConfig.class);
                if (blacklistConfig == null) {
                    blacklistConfig = new BlacklistConfig();
                    JustAnotherSurvivalDebugStick.LOGGER.warn("blacklist.json is empty - created default blacklist");
                }
            } catch (IOException e) {
                JustAnotherSurvivalDebugStick.LOGGER.error("Failed to read blacklist.json", e);
                blacklistConfig = new BlacklistConfig();
            }
            saveBlacklist();
        }

        if (!whitelistFile.exists()) {
            whitelistConfig = new WhitelistConfig();
            saveWhitelist();
            JustAnotherSurvivalDebugStick.LOGGER.info("Created default whitelist.json");
        } else {
            try (FileReader reader = new FileReader(whitelistFile)) {
                whitelistConfig = GSON.fromJson(reader, WhitelistConfig.class);
                if (whitelistConfig == null) {
                    whitelistConfig = new WhitelistConfig();
                    JustAnotherSurvivalDebugStick.LOGGER.warn("whitelist.json is empty - created default whitelist");
                }
            } catch (IOException e) {
                JustAnotherSurvivalDebugStick.LOGGER.error("Failed to read whitelist.json", e);
                whitelistConfig = new WhitelistConfig();
            }
            saveWhitelist();
        }
    }

    public static void saveConfig() {
        try (FileWriter writer = new FileWriter(configFile)) {
            GSON.toJson(config, writer);
        } catch (IOException e) {
            JustAnotherSurvivalDebugStick.LOGGER.error("Failed to save config.json", e);
        }
    }

    public static void saveWhitelist() {
        try (FileWriter writer = new FileWriter(whitelistFile)) {
            GSON.toJson(whitelistConfig, writer);
        } catch (IOException e) {
            JustAnotherSurvivalDebugStick.LOGGER.error("Failed to save whitelist.json", e);
        }
    }

    public static void saveBlacklist() {
        try (FileWriter writer = new FileWriter(blacklistFile)) {
            GSON.toJson(blacklistConfig, writer);
        } catch (IOException e) {
            JustAnotherSurvivalDebugStick.LOGGER.error("Failed to save blacklist.json", e);
        }
    }

    public static JASDSConfig getConfig() {
        if (config == null) loadConfig();
        return config;
    }

    public static WhitelistConfig getWhitelist() {
        if (whitelistConfig == null) loadConfig();
        return whitelistConfig;
    }

    public static BlacklistConfig getBlacklist() {
        if (blacklistConfig == null) loadConfig();
        return blacklistConfig;
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