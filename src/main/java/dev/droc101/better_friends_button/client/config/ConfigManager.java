package dev.droc101.better_friends_button.client.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.droc101.better_friends_button.client.BetterFriendsButton;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class ConfigManager {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final File CONFIG_FILE = new File(FabricLoader.getInstance().getConfigDir().toFile(), "better_friends_button_config.json");

    private static Config config;

    public static void loadConfig(boolean replaceInvalid) {
        if (CONFIG_FILE.exists()) {
            try (FileReader reader = new FileReader(CONFIG_FILE)) {
                config = GSON.fromJson(reader, Config.class);
            } catch (IOException e) {
                BetterFriendsButton.LOGGER.error("Failed to load config!", e);
                if (replaceInvalid) {
                    config = Config.getDefault();
                }
                return;
            }
        } else {
            config = Config.getDefault();
            saveConfig();
        }
        if (config == null) {
            BetterFriendsButton.LOGGER.error("Failed to load config!");
            if (replaceInvalid) {
                config = Config.getDefault();
            }
            return;
        }
        saveConfig();
    }

    public static void saveConfig() {
        try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
            GSON.toJson(config, writer);
        } catch (IOException e) {
            BetterFriendsButton.LOGGER.error("Failed to save config, will use the default.", e);
        }
    }

    public static Config getConfig() {
        return config;
    }

}
