package net.thiagoaa.redstonetimer.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.thiagoaa.redstonetimer.RedstoneTimer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class RedstoneTimerConfig {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = FabricLoader.getInstance()
            .getConfigDir()
            .resolve(RedstoneTimer.MOD_ID + ".json");

    public static boolean blockSound = true;

    static {
        load();
    }

    public static void load() {
        if (!Files.exists(CONFIG_PATH)) {
            save();
            return;
        }

        try {
            String json = Files.readString(CONFIG_PATH);
            Data data = GSON.fromJson(json, Data.class);

            if (data != null) {
                blockSound = data.blockSound;
            }
        } catch (IOException e) {
            RedstoneTimer.LOGGER.error("Failed to load Redstone Timer config", e);
        }
    }

    public static void save() {
        try {
            Data data = new Data();
            data.blockSound = blockSound;

            Files.writeString(CONFIG_PATH, GSON.toJson(data));
        } catch (IOException e) {
            RedstoneTimer.LOGGER.error("Failed to save Redstone Timer config", e);
        }
    }

    private static class Data {
        boolean blockSound = true;
    }
}