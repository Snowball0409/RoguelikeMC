package snowball049.roguelikemc.config;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.nio.file.Path;
import java.util.Map;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import snowball049.roguelikemc.RoguelikeMC;

public class RoguelikeMCUpgradesConfig {

    // Config file path
    public static final Path UPGRADE_CONFIG_PATH = FabricLoader.getInstance()
            .getConfigDir().resolve(RoguelikeMC.MOD_ID+"/"+RoguelikeMC.MOD_ID+"-upgrades.json");
    public static RoguelikeMCUpgradesConfig INSTANCE = new RoguelikeMCUpgradesConfig();

    // Upgrade Config Model
    public Map<String, RogueLikeMCUpgradeConfig> upgrades = new HashMap<>();

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
        String tier,
        boolean is_permanent,
        boolean is_unique,
        String icon,
        List<UpgradeAction> action) {
        public static final Codec<RogueLikeMCUpgradeConfig> CODEC = RecordCodecBuilder.create(
                instance -> instance.group(
                        Codec.STRING.fieldOf("id").forGetter(RogueLikeMCUpgradeConfig::id),
                        Codec.STRING.fieldOf("name").forGetter(RogueLikeMCUpgradeConfig::name),
                        Codec.STRING.fieldOf("description").forGetter(RogueLikeMCUpgradeConfig::description),
                        Codec.STRING.fieldOf("tier").forGetter(RogueLikeMCUpgradeConfig::tier),
                        Codec.BOOL.fieldOf("is_permanent").forGetter(RogueLikeMCUpgradeConfig::is_permanent),
                        Codec.BOOL.fieldOf("is_unique").forGetter(RogueLikeMCUpgradeConfig::is_unique),
                        Codec.STRING.fieldOf("icon").forGetter(RogueLikeMCUpgradeConfig::icon),
                        Codec.list(UpgradeAction.CODEC).fieldOf("action").forGetter(RogueLikeMCUpgradeConfig::action)
                ).apply(instance, RogueLikeMCUpgradeConfig::new)
        );
        public static final PacketCodec<RegistryByteBuf, RogueLikeMCUpgradeConfig> PACKET_CODEC = PacketCodecs.registryCodec(RogueLikeMCUpgradeConfig.CODEC);
    }


    public static void loadConfig() {
        Gson gson = new GsonBuilder().setLenient().setPrettyPrinting().create();
        File file = UPGRADE_CONFIG_PATH.toFile();

        try {
            Files.createDirectories(file.getParentFile().toPath());
        } catch (IOException e) {
            RoguelikeMC.LOGGER.error("Failed to create config directory", e);
            return;
        }

        if (file.exists()) {
            try (FileReader fileReader = new FileReader(file)) {
                try (JsonReader reader = new JsonReader(fileReader)) {
                    RoguelikeMCUpgradesConfig tempConfig = gson.fromJson(reader, RoguelikeMCUpgradesConfig.class);

                    // 確保 HashMap 格式正確
                    Type type = new TypeToken<HashMap<String, RogueLikeMCUpgradeConfig>>() {}.getType();
                    String jsonString = gson.toJson(tempConfig.upgrades); // 轉成 JSON 字串
                    tempConfig.upgrades = gson.fromJson(jsonString, type); // 重新解析成 HashMap

                    INSTANCE = tempConfig;
                }
            } catch (IOException e) {
                RoguelikeMC.LOGGER.error("Failed to load config: ", e);
            }
        } else {
            writeConfig(gson, file, INSTANCE);
        }
    }

    public static void writeConfig(Gson gson, File file, RoguelikeMCUpgradesConfig config){
        try(FileWriter writer = new FileWriter(file)) {
            writeDefaultConfig(config);
            gson.toJson(config, writer);

            RoguelikeMC.LOGGER.info("Successfully wrote config to file {}", file.getName());
        } catch (IOException e) {
            RoguelikeMC.LOGGER.error("Failed to write config: ", e);
        }
    }

    private static void writeDefaultConfig(RoguelikeMCUpgradesConfig config) {
        final RogueLikeMCUpgradeConfig health_1 = new RogueLikeMCUpgradeConfig(
                "health_1",
                "+1 Health",
                "Add 1 Health",
                "common",
                false,
                false,
                "minecraft:textures/mob_effect/regeneration.png",
                List.of(
                        new UpgradeAction("attribute", List.of("minecraft:generic.max_health", "1", "add_value"))
                )
        );
        final RogueLikeMCUpgradeConfig attack_1 = new RogueLikeMCUpgradeConfig(
                "attack_1",
                "+1 Attack",
                "Add 1 Attack",
                "common",
                false,
                false,
                "minecraft:textures/mob_effect/strength.png",
                List.of(
                        new UpgradeAction("attribute", List.of("minecraft:generic.attack_damage", "1", "add_value"))
                )
        );
        final RogueLikeMCUpgradeConfig speed_10 = new RogueLikeMCUpgradeConfig(
                "speed_10",
                "+10% Speed",
                "Add 10% Speed",
                "common",
                true,
                false,
                "minecraft:textures/mob_effect/speed.png",
                List.of(
                        new UpgradeAction("attribute", List.of("minecraft:generic.movement_speed", "0.1", "add_multiplied_total"))
                )
        );
        final RogueLikeMCUpgradeConfig dragon_skin = new RogueLikeMCUpgradeConfig(
                "dragon_skin",
                "Dragon Skin",
                "Get resistant I but minus 1 Heart",
                "rare",
                false,
                true,
                "minecraft:textures/item/dragon_breath.png",
                List.of(
                        new UpgradeAction("attribute", List.of("minecraft:generic.max_health", "-2", "add_value")),
                        new UpgradeAction("effect", List.of("minecraft:resistance", "-1", "0"))
                )
        );
        final RogueLikeMCUpgradeConfig phoenix_feather = new RogueLikeMCUpgradeConfig(
                "phoenix_feather",
                "Phoenix Feather",
                "Grants regeneration effect and feather falling effect",
                "epic",
                false,
                false,
                "minecraft:textures/item/feather.png",
                List.of(
                        new UpgradeAction("effect", List.of("minecraft:regeneration", "-1", "0")),
                        new UpgradeAction("effect", List.of("minecraft:slow_falling", "-1", "0"))
                )
        );
        final RogueLikeMCUpgradeConfig titan_strength = new RogueLikeMCUpgradeConfig(
                "titan_strength",
                "Titan Strength",
                "Increases attack damage by 5",
                "legendary",
                true,
                false,
                "minecraft:textures/mob_effect/strength.png",
                List.of(
                        new UpgradeAction("attribute", List.of("minecraft:generic.attack_damage", "5", "add_value"))
                )
        );

        final RogueLikeMCUpgradeConfig swift_boots = new RogueLikeMCUpgradeConfig(
                "swift_boots",
                "Swift Boots",
                "Increases movement speed by 10%",
                "common",
                false,
                true,
                "minecraft:textures/mob_effect/speed.png",
                List.of(
                        new UpgradeAction("attribute", List.of("minecraft:generic.movement_speed", "0.1", "add_multiplied_base"))
                )
        );

        final RogueLikeMCUpgradeConfig iron_hide = new RogueLikeMCUpgradeConfig(
                "iron_hide",
                "Iron Hide",
                "Increases armor by 3",
                "rare",
                false,
                false,
                "minecraft:textures/item/iron_ingot.png",
                List.of(
                        new UpgradeAction("attribute", List.of("minecraft:generic.armor", "3", "add_value"))
                )
        );

        final RogueLikeMCUpgradeConfig shadow_cloak = new RogueLikeMCUpgradeConfig(
                "shadow_cloak",
                "Shadow Cloak",
                "Grants invisibility effect for 5 seconds",
                "epic",
                true,
                true,
                "minecraft:textures/mob_effect/invisibility.png",
                List.of(
                        new UpgradeAction("effect", List.of("minecraft:invisibility", "-1", "0"))
                )
        );

        final RogueLikeMCUpgradeConfig fire_touch = new RogueLikeMCUpgradeConfig(
                "fire_touch",
                "Fire Touch",
                "Sets enemy on fire when hit",
                "rare",
                false,
                false,
                "minecraft:textures/mob_effect/fire_resistance.png",
                List.of(
//                        new UpgradeAction("event", List.of("roguelikemc:set_on_fire", "5", "0"))
                )
        );

        final RogueLikeMCUpgradeConfig lifesteal = new RogueLikeMCUpgradeConfig(
                "lifesteal",
                "Life Steal",
                "Heal for 20% of damage dealt",
                "legendary",
                true,
                false,
                "minecraft:textures/mob_effect/health_boost.png",
                List.of(
//                        new UpgradeAction("event", List.of("roguelikemc:lifesteal", "20", "0"))
                )
        );

        final RogueLikeMCUpgradeConfig arcane_barrier = new RogueLikeMCUpgradeConfig(
                "arcane_barrier",
                "Arcane Barrier",
                "Grants absorption for 30 seconds",
                "epic",
                false,
                true,
                "minecraft:textures/item/ender_eye.png",
                List.of(
                        new UpgradeAction("effect", List.of("minecraft:absorption", "-1", "0"))
                )
        );

        final RogueLikeMCUpgradeConfig berserker_rage = new RogueLikeMCUpgradeConfig(
                "berserker_rage",
                "Berserker Rage",
                "Increases attack speed but reduces defense",
                "legendary",
                true,
                true,
                "minecraft:textures/item/golden_sword.png",
                List.of(
                        new UpgradeAction("attribute", List.of("minecraft:generic.attack_speed", "0.5", "add_value")),
                        new UpgradeAction("attribute", List.of("minecraft:generic.armor", "-2", "add_value"))
                )
        );

        final RogueLikeMCUpgradeConfig arcane_focus = new RogueLikeMCUpgradeConfig(
                "arcane_focus",
                "Arcane Focus",
                "Increases mana regeneration by 15%",
                "common",
                false,
                true,
                "minecraft:textures/item/ender_pearl.png",
                List.of(
                        new UpgradeAction("attribute", List.of("minecraft:generic.max_health", "1", "add_value")),
                        new UpgradeAction("effect", List.of("minecraft:haste", "-1", "0"))
                )
        );

        config.upgrades.put(health_1.id, health_1);
        config.upgrades.put(attack_1.id, attack_1);
        config.upgrades.put(speed_10.id, speed_10);
        config.upgrades.put(dragon_skin.id, dragon_skin);
        config.upgrades.put(phoenix_feather.id, phoenix_feather);
        config.upgrades.put(titan_strength.id, titan_strength);
        config.upgrades.put(swift_boots.id, swift_boots);
        config.upgrades.put(iron_hide.id, iron_hide);
        config.upgrades.put(shadow_cloak.id, shadow_cloak);
        config.upgrades.put(fire_touch.id, fire_touch);
        config.upgrades.put(lifesteal.id, lifesteal);
        config.upgrades.put(arcane_barrier.id, arcane_barrier);
        config.upgrades.put(berserker_rage.id, berserker_rage);
        config.upgrades.put(arcane_focus.id, arcane_focus);
    }
}