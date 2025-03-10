package snowball049.roguelikemc.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.nio.file.Path;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import snowball049.roguelikemc.RoguelikeMC;

public class RoguelikeMCConfig {

    // Config file path
    public static final Path UPGRADE_CONFIG_PATH = FabricLoader.getInstance()
            .getConfigDir().resolve(RoguelikeMC.MOD_ID+"/"+RoguelikeMC.MOD_ID+"-upgrade.json");
    public static RoguelikeMCConfig INSTANCE = new RoguelikeMCConfig();

    // Upgrade Config Model
    public List<RogueLikeMCUpgradeConfig> upgrades = new ArrayList<>();

    public record UpgradeAction(String type, List<String> value) {
        public static final Codec<UpgradeAction> CODEC = RecordCodecBuilder.create(
                instance -> instance.group(
                        Codec.STRING.fieldOf("type").forGetter(UpgradeAction::type),
                        Codec.list(Codec.STRING).fieldOf("value").forGetter(UpgradeAction::value)
                ).apply(instance, UpgradeAction::new)
        );
    }

    public record RogueLikeMCUpgradeConfig(
        String id,
        String name,
        String description,
        boolean is_permanent,
        String icon,
        String tier,
        List<UpgradeAction> action) {
        public static final Codec<RogueLikeMCUpgradeConfig> CODEC = RecordCodecBuilder.create(
                instance -> instance.group(
                        Codec.STRING.fieldOf("id").forGetter(RogueLikeMCUpgradeConfig::id),
                        Codec.STRING.fieldOf("name").forGetter(RogueLikeMCUpgradeConfig::name),
                        Codec.STRING.fieldOf("description").forGetter(RogueLikeMCUpgradeConfig::description),
                        Codec.BOOL.fieldOf("is_permanent").forGetter(RogueLikeMCUpgradeConfig::is_permanent),
                        Codec.STRING.fieldOf("icon").forGetter(RogueLikeMCUpgradeConfig::icon),
                        Codec.STRING.fieldOf("tier").forGetter(RogueLikeMCUpgradeConfig::tier),
                        Codec.list(UpgradeAction.CODEC).fieldOf("action").forGetter(RogueLikeMCUpgradeConfig::action)
                ).apply(instance, RogueLikeMCUpgradeConfig::new)
        );
        public static final PacketCodec<RegistryByteBuf, RogueLikeMCUpgradeConfig> PACKET_CODEC = PacketCodecs.registryCodec(RogueLikeMCUpgradeConfig.CODEC);
    }


    public static void loadConfig() {
        Gson gson = new GsonBuilder().setLenient().setPrettyPrinting().create();
        File file = UPGRADE_CONFIG_PATH.toFile();
        if (file.exists()) {
            try(FileReader fileReader = new FileReader(file)) {
                try(JsonReader reader = new JsonReader(fileReader)){
                    INSTANCE = gson.fromJson(reader, RoguelikeMCConfig.class);
                    //writeConfig(gson, file, INSTANCE);
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
            writeDefaultConfig(config);
            gson.toJson(config, writer);

            RoguelikeMC.LOGGER.info("Successfully wrote config to file {}", file.getName());
        } catch (IOException e) {
            RoguelikeMC.LOGGER.error("Failed to write config: ", e);
        }
    }

    private static void writeDefaultConfig(RoguelikeMCConfig config) {
        final RogueLikeMCUpgradeConfig health_1 = new RogueLikeMCUpgradeConfig(
                "health_1",
                "+1 Health",
                "Add 1 Health",
                true,
                "minecraft:textures/mob_effect/regeneration.png",
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
                "minecraft:textures/mob_effect/strength.png",
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
                "minecraft:textures/mob_effect/speed.png",
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
                "minecraft:textures/item/dragon_breath.png",
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
                "minecraft:textures/mob_effect/absorption.png",
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
                "minecraft:textures/mob_effect/strength.png",
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
                "minecraft:textures/mob_effect/speed.png",
                "rare",
                List.of(
                        new UpgradeAction("attribute", List.of("minecraft:generic.movement_speed", "0.2", "add_multiplied_total"))
                )
        );
        config.upgrades.add(health_1);
        config.upgrades.add(attack_1);
        config.upgrades.add(speed_10);
        config.upgrades.add(dragon_skin);
        config.upgrades.add(health_2);
        config.upgrades.add(attack_2);
        config.upgrades.add(speed_20);
    }
}