package snowball049.roguelikemc.config;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.moandjiezana.toml.Toml;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RoguelikeMCConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(RoguelikeMCConfig.class);

    public static class UpgradeAction {
        public String type;
        public List<String> value;
    }

    public static class UpgradeOption {
        public String name;
        public String description;
        public List<UpgradeAction> action;
    }

    public static class UpgradeConfig {
        public List<UpgradeOption> commonUpgrade;
        public List<UpgradeOption> rareUpgrade;
        public List<UpgradeOption> legendaryUpgrade;
    }

    private static final String CONFIG_PATH = FabricLoader.getInstance()
            .getConfigDir().resolve("roguelikemc/roguelikemc-common.toml").toString();

    public static UpgradeConfig loadConfig() {
        File configFile = new File(CONFIG_PATH);
        if (!configFile.exists()) {
            LOGGER.warn("Config file not found: {}. Generating default config...", CONFIG_PATH);
            generateDefaultConfig();
        }

        Toml toml = new Toml();
        try (FileReader reader = new FileReader(configFile)) {
            toml.read(reader);
            UpgradeConfig config = new UpgradeConfig();
            config.commonUpgrade = parseUpgradeOptions(toml.getList("roguelikemc_upgrade.commonUpgrade", new ArrayList<>()));
            config.rareUpgrade = parseUpgradeOptions(toml.getList("roguelikemc_upgrade.rareUpgrade", new ArrayList<>()));
            config.legendaryUpgrade = parseUpgradeOptions(toml.getList("roguelikemc_upgrade.legendaryUpgrade", new ArrayList<>()));
            return config;
        } catch (IOException e) {
            LOGGER.error("Failed to load config: ", e);
            return null;
        }
    }

    public static void generateDefaultConfig() {
        String defaultConfig = """
                [roguelikemc_upgrade]
                   # Upgrade options for different tier
                   # You can add more options by adding more JSON strings like below format
                   # Type has three options: "attribute", "effect", "command", "event"
                   # More detail can be found in the README
                  \s
                   commonUpgrade = [
                       '''
                       {
                           "name": "+1 Health",
                           "description": "Add 1 Health",
                           "action": [
                               {
                                   "type": "attribute",
                                   "value": ["minecraft:generic.max_health", "1", "add_value"]
                               }
                           ]
                       }
                       ''',
                       '''
                       {
                           "name": "+1 Attack",
                           "description": "Add 1 Attack",
                           "action": [
                               {
                                   "type": "attribute",
                                   "value": ["minecraft:generic.attack_damage", "1", "add_value"]
                               }
                           ]
                       }
                       ''',
                       '''
                       {
                           "name": "+10% Speed",
                           "description": "Add 10% Speed",
                           "action": [
                               {
                                   "type": "attribute",
                                   "value": ["minecraft:generic.movement_speed", "0.1", "add_multiplied_total"]
                               }
                           ]
                       }
                       '''
                   ]
                  \s
                   rareUpgrade = [
                       '''
                       {
                           "name": "Dragon Skin",
                           "description": "Get resistant I but minus 1 Heart",
                           "action": [
                               {
                                   "type": "attribute",
                                   "value": ["minecraft:generic.max_health", "-2", "add_value"]
                               },
                               {
                                   "type": "effect",
                                   "value": ["minecraft:resistance", "999999", "0"]
                               }
                           ]
                       }
                       '''
                   ]
                  \s
                   legendaryUpgrade = []
            """;

        try {
            Files.createDirectories(Paths.get("config/roguelikemc"));
            try (FileWriter writer = new FileWriter(CONFIG_PATH)) {
                writer.write(defaultConfig);
                LOGGER.info("Default config generated at: {}", CONFIG_PATH);
            }
        } catch (IOException e) {
            LOGGER.error("Failed to generate default config: ", e);
        }
    }

    public static List<UpgradeOption> parseUpgradeOptions(List<String> jsonStrings) {
        Gson gson = new Gson();
        Type listType = new TypeToken<List<UpgradeOption>>() {}.getType();
        List<UpgradeOption> upgrades = new ArrayList<>();
        for (String json : jsonStrings) {
            try {
                UpgradeOption option = gson.fromJson(json, UpgradeOption.class);
                upgrades.add(option);
            } catch (Exception e) {
                LOGGER.warn("Failed to parse upgrade option: {}", json, e);
            }
        }
        return upgrades;
    }
}