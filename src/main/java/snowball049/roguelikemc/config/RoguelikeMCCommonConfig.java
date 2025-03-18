package snowball049.roguelikemc.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import net.fabricmc.loader.api.FabricLoader;
import snowball049.roguelikemc.RoguelikeMC;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class RoguelikeMCCommonConfig {

    // Config file path
    public static final Path COMMON_CONFIG_PATH = FabricLoader.getInstance()
            .getConfigDir().resolve(RoguelikeMC.MOD_ID + "/" + RoguelikeMC.MOD_ID + "-common.json");
    public static RoguelikeMCCommonConfig INSTANCE = new RoguelikeMCCommonConfig();

    // Common Config Model
    public boolean enableUpgradeSystem = true;
    public boolean enableKillHostileEntityUpgrade = true;
    public boolean enableAdvancementUpgrade = false;
    public boolean enableLevelUpgrade = false;
    public boolean enableClearInventoryAfterDeath = false;
    public boolean enableClearEquipmentAfterDeath = false;
    public boolean enableDecayInventoryAfterDeath = true;
    public boolean enableDecayEquipmentAfterDeath = true;
    public boolean enableLinearGameStage = true;

    public List<Integer> killHostileEntityRequirementMinMax = List.of(10, 30);
    public Integer amountBetweenKillHostileEntityUpgrade = 5;
    public Integer amountOfLevelUpgrade = 5;
    public double decayInventoryPercentage = 0.6;
    public String decayItem = "minecraft:rotten_flesh";
    public List<Integer> decayItemAmountMinMax = List.of(1, 3);
    public List<String> gameStageEntities = List.of("minecraft:wither", "minecraft:ender_dragon");
    public double gameStageDecayPercentage = 0.9;

    // Config read and write
    public static void loadConfig() {
        Gson gson = new GsonBuilder().setLenient().setPrettyPrinting().create();
        File file = COMMON_CONFIG_PATH.toFile();

        try {
            Files.createDirectories(file.getParentFile().toPath());
        } catch (IOException e) {
            RoguelikeMC.LOGGER.error("Failed to create config directory", e);
            return;
        }

        if (file.exists()) {
            try (FileReader reader = new FileReader(file)) {
                try (JsonReader jsonReader = new JsonReader(reader)) {
                    INSTANCE = gson.fromJson(jsonReader, RoguelikeMCCommonConfig.class);
                    writeConfig(gson, file, INSTANCE);
                }
            } catch (IOException e) {
                RoguelikeMC.LOGGER.error("Failed to load config file", e);
            }
        } else {
            writeConfig(gson, file, INSTANCE);
        }
    }

    private static void writeConfig(Gson gson, File file, RoguelikeMCCommonConfig config) {
        try (FileWriter writer = new FileWriter(file)) {
            gson.toJson(config, writer);
            RoguelikeMC.LOGGER.info("Successfully wrote to config file {}", file);
        } catch (IOException e) {
            RoguelikeMC.LOGGER.error("Failed to write config file", e);
        }
    }
}