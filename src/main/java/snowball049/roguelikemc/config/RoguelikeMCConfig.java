package snowball049.roguelikemc.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.moandjiezana.toml.Toml;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.nio.file.Path;

import net.fabricmc.loader.api.FabricLoader;
import snowball049.roguelikemc.RoguelikeMC;

public class RoguelikeMCConfig {

    // Config file path
    public static final Path UPGRADE_CONFIG_PATH = FabricLoader.getInstance()
            .getConfigDir().resolve(RoguelikeMC.MOD_ID+"/"+RoguelikeMC.MOD_ID+"-upgrade.json");
    public static RoguelikeMCConfig INSTANCE = new RoguelikeMCConfig();


    // Upgrade Config Model
    public RogueLikeMCUpgradesConfig upgradesConfig = new RogueLikeMCUpgradesConfig();

    public static class RogueLikeMCUpgradesConfig {
        public List<RogueLikeMCUpgradeConfig> upgrades = new ArrayList<>();
    }

    public record RogueLikeMCUpgradeConfig(
        String id,
        String name,
        String description,
        boolean is_permanent,
        String icon,
        String tier,
        List<UpgradeAction> action) {
    }
    public record UpgradeAction(String type, List<String> value) {
    }

    public static void loadConfig() {
        Gson gson = new GsonBuilder().setLenient().setPrettyPrinting().create();
        File file = UPGRADE_CONFIG_PATH.toFile();
        if (file.exists()) {
            try(FileReader fileReader = new FileReader(file)) {
                try(JsonReader reader = new JsonReader(fileReader)){
                    INSTANCE = gson.fromJson(reader, RoguelikeMCConfig.class);
                    writeConfig(gson, file, INSTANCE);
                }
            }catch(IOException e){
                RoguelikeMC.LOGGER.error("Failed to load config: ", e);
            }
        }else{
            writeConfig(gson, file, INSTANCE);
        }
    }

    public static void writeConfig(Gson gson, File file, RoguelikeMCConfig config){
        try(FileWriter writer = new FileWriter(file)) {
            final RogueLikeMCUpgradeConfig health_1 = new RogueLikeMCUpgradeConfig(
                    "health_1",
                    "+1 Health",
                    "Add 1 Health",
                    true,
                    "minecraft:apple",
                    "common",
                    List.of(
                            new UpgradeAction("attribute", List.of("minecraft:generic.max_health", "1", "add_value"))
                    )
            );
            final RogueLikeMCUpgradeConfig attack_1 = new RogueLikeMCUpgradeConfig(
                    "attack_1",
                    "+1 Attack",
                    "Add 1 Attack",
                    true,
                    "minecraft:sword",
                    "common",
                    List.of(
                            new UpgradeAction("attribute", List.of("minecraft:generic.attack_damage", "1", "add_value"))
                    )
            );
            final RogueLikeMCUpgradeConfig speed_10 = new RogueLikeMCUpgradeConfig(
                    "speed_10",
                    "+10% Speed",
                    "Add 10% Speed",
                    true,
                    "minecraft:feather",
                    "common",
                    List.of(
                            new UpgradeAction("attribute", List.of("minecraft:generic.movement_speed", "0.1", "add_multiplied_total"))
                    )
            );
            final RogueLikeMCUpgradeConfig dragon_skin = new RogueLikeMCUpgradeConfig(
                    "dragon_skin",
                    "Dragon Skin",
                    "Get resistant I but minus 1 Heart",
                    true,
                    "minecraft:dragon_head",
                    "rare",
                    List.of(
                            new UpgradeAction("attribute", List.of("minecraft:generic.max_health", "-2", "add_value")),
                            new UpgradeAction("effect", List.of("minecraft:resistance", "999999", "0"))
                    )
            );
            final RogueLikeMCUpgradeConfig health_2 = new RogueLikeMCUpgradeConfig(
                    "health_2",
                    "+2 Health",
                    "Add 2 Health",
                    true,
                    "minecraft:golden_apple",
                    "rare",
                    List.of(
                            new UpgradeAction("attribute", List.of("minecraft:generic.max_health", "2", "add_value"))
                    )
            );
            final RogueLikeMCUpgradeConfig attack_2 = new RogueLikeMCUpgradeConfig(
                    "attack_2",
                    "+2 Attack",
                    "Add 2 Attack",
                    true,
                    "minecraft:diamond_sword",
                    "rare",
                    List.of(
                            new UpgradeAction("attribute", List.of("minecraft:generic.attack_damage", "2", "add_value"))
                    )
            );
            final RogueLikeMCUpgradeConfig speed_20 = new RogueLikeMCUpgradeConfig(
                    "speed_20",
                    "+20% Speed",
                    "Add 20% Speed",
                    true,
                    "minecraft:elytra",
                    "rare",
                    List.of(
                            new UpgradeAction("attribute", List.of("minecraft:generic.movement_speed", "0.2", "add_multiplied_total"))
                    )
            );
            config.upgradesConfig.upgrades.add(health_1);
            config.upgradesConfig.upgrades.add(attack_1);
            config.upgradesConfig.upgrades.add(speed_10);
            config.upgradesConfig.upgrades.add(dragon_skin);
            config.upgradesConfig.upgrades.add(health_2);
            config.upgradesConfig.upgrades.add(attack_2);
            config.upgradesConfig.upgrades.add(speed_20);

            gson.toJson(config, writer);
        } catch (IOException e) {
            RoguelikeMC.LOGGER.error("Failed to write config: ", e);
        }
    }
}