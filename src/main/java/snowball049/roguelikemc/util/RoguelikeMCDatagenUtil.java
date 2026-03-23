package snowball049.roguelikemc.util;

import snowball049.roguelikemc.data.RoguelikeMCUpgradeData;
import snowball049.roguelikemc.datagen.RoguelikeMCUpgradeDataProvider;

import java.util.List;

public class RoguelikeMCDatagenUtil {
    public static void addDefaultUpgrades(RoguelikeMCUpgradeDataProvider upgradeProvider) {
        List<RoguelikeMCUpgradeData> defaultUpgrades = List.of(
                upgrade("fortunes_favor", "rare", true, false, List.of(attribute("minecraft:generic.luck", "1", "add_value"))),
                upgrade("swift_stride", "common", true, false, List.of(attribute("minecraft:generic.movement_speed", "0.04", "add_multiplied_base"))),
                upgrade("enduring_vitality", "common", true, false, List.of(attribute("minecraft:generic.max_health", "2", "add_value"))),
                upgrade("blade_dancer", "common", true, false, List.of(attribute("minecraft:generic.attack_speed", "0.05", "add_multiplied_base"))),
                upgrade("mighty_force", "common", true, false, List.of(attribute("minecraft:generic.attack_damage", "0.05", "add_multiplied_base"))),
                upgrade("adamant_guard", "common", true, false, List.of(attribute("minecraft:generic.armor", "1", "add_value"))),
                upgrade("wisdoms_bounty", "common", true, false, List.of(attribute("roguelikemc:experience_gain", "0.06", "add_value"))),

                upgrade("skyborn", "legendary", false, true, List.of(
                        attribute("minecraft:generic.max_health", "-0.5", "add_multiplied_base"),
                        event("allow_creative_flying")
                )),
                upgrade("undying_will", "legendary", false, true, List.of(event("keep_equipment_after_death"))),
                upgrade("one_last_chance", "legendary", false, true, List.of(event("one_last_chance"))),

                upgrade("berserkers_wrath", "epic", false, false, List.of(
                        attribute("minecraft:generic.attack_damage", "0.5", "add_multiplied_base"),
                        attribute("roguelikemc:damage_ratio", "0.5", "add_value")
                )),
                upgrade("winged_warrior", "epic", false, true, List.of(
                        event("set_equipment", "2", "{components: {\"minecraft:enchantments\": {levels: {\"minecraft:binding_curse\": 1}}, \"minecraft:unbreakable\": {}}, count: 1, id: \"minecraft:elytra\"}")
                )),
                upgrade("infernal_strider", "epic", false, true, List.of(
                        event("set_equipment", "0", "{components: {\"minecraft:enchantments\": {levels: {\"minecraft:frost_walker\": 2, \"minecraft:binding_curse\": 1, \"minecraft:protection\": 5}}, \"minecraft:unbreakable\": {}}, count: 1, id: \"minecraft:netherite_boots\"}")
                )),
                upgrade("heavy_appetite", "epic", false, true, List.of(
                        effect("minecraft:saturation", "-1", "0"),
                        attribute("minecraft:generic.movement_speed", "-0.5", "add_multiplied_base")
                )),
                upgrade("last_stand", "epic", false, true, List.of(effect("minecraft:regeneration", "-1", "0"))),

                upgrade("turtles_blessing", "rare", false, true, List.of(
                        event("set_equipment", "3", "{components: {\"minecraft:enchantments\": {levels: {\"minecraft:binding_curse\": 1}}, \"minecraft:unbreakable\": {}}, count: 1, id: \"minecraft:turtle_helmet\"}")
                )),
                upgrade("stonebound", "rare", false, true, List.of(
                        attribute("minecraft:generic.max_health", "-2", "add_value"),
                        effect("minecraft:resistance", "-1", "0")
                )),
                upgrade("tank_mode", "rare", false, true, List.of(
                        attribute("minecraft:generic.max_health", "4", "add_value"),
                        effect("minecraft:resistance", "-1", "1"),
                        event("set_equipment", "1", "")
                )),
                upgrade("villager_slayer", "rare", false, true, List.of(
                        event("add_loot_table", "minecraft:villager", "roguelikemc:villager_slayer"),
                        event("provoked", "minecraft:iron_golem")
                )),
                upgrade("mystic_steed", "rare", false, false, List.of(
                        command("summon horse ~ ~ ~ {Tame:1b,SaddleItem:{id:\"minecraft:saddle\",count:1},attributes:[{id:\"minecraft:generic.jump_strength\",base:5},{id:\"minecraft:generic.movement_speed\",base:0.05},{id:\"minecraft:generic.fall_damage_multiplier\", base:0}]}")
                )),
                upgrade("titans_bulk", "rare", false, false, List.of(
                        attribute("minecraft:generic.scale", "1", "add_multiplied_total"),
                        attribute("minecraft:generic.movement_speed", "-0.2", "add_multiplied_base"),
                        attribute("minecraft:generic.attack_damage", "0.2", "add_multiplied_base")
                )),
                upgrade("pixie_form", "rare", false, false, List.of(
                        attribute("minecraft:generic.scale", "-0.5", "add_multiplied_total"),
                        attribute("minecraft:generic.movement_speed", "0.2", "add_multiplied_base"),
                        attribute("minecraft:generic.attack_damage", "-0.2", "add_multiplied_base")
                )),

                upgrade("toxic_presence", "rare", false, true, List.of(event("effect_mobs", "minecraft:poison", "0", "8.0"))),
                upgrade("withering_aura", "epic", false, true, List.of(event("effect_mobs", "minecraft:wither", "0", "8.0"))),
                upgrade("nocturnal_sight", "common", false, true, List.of(effect("minecraft:night_vision", "-1", "0"))),
                upgrade("eternal_companion", "common", false, false, List.of(command("summon cat ~ ~ ~ {Invulnerable:1b}", true))),
                upgrade("leap_of_faith", "common", false, true, List.of(effect("minecraft:jump_boost", "-1", "0"))),
                upgrade("feathers_grace", "common", false, true, List.of(effect("minecraft:slow_falling", "-1", "0"))),
                upgrade("equestrian_gift", "common", false, false, List.of(command("summon horse ~ ~ ~ {Tame:1b,SaddleItem:{id:\"minecraft:saddle\",count:1}}"))),
                upgrade("golden_fortune", "common", false, false, List.of(command("give @s golden_apple 16"))),
                upgrade("golden_harvest", "common", false, false, List.of(command("give @s golden_carrot 64"))),
                upgrade("gods_fruit", "common", false, false, List.of(command("give @s enchanted_golden_apple 1"))),
                upgrade("miners_frenzy", "common", false, true, List.of(effect("minecraft:haste", "-1", "0"))),
                upgrade("scholars_gift", "common", false, false, List.of(command("give @p enchanted_book[stored_enchantments={\"minecraft:mending\":1}] 1"))),
                upgrade("prospectors_luck", "common", false, false, List.of(command("loot give @s loot roguelikemc:random_ores"))),
                upgrade("brutes_strength", "common", false, true, List.of(effect("minecraft:strength", "-1", "0"))),
                upgrade("feather_bound", "epic", false, true, List.of(attribute("minecraft:generic.fall_damage_multiplier", "-1", "add_value"))),
                upgrade("tide_glider", "common", true, false, List.of(attribute("minecraft:generic.water_movement_efficiency", "0.33", "add_value"))),
                upgrade("iron_stance", "common", true, false, List.of(attribute("minecraft:generic.knockback_resistance", "0.1", "add_value"))),
                upgrade("deep_lungs", "common", true, false, List.of(attribute("minecraft:generic.oxygen_bonus", "1", "add_value"))),
                upgrade("steel_hide", "common", true, false, List.of(attribute("minecraft:generic.armor_toughness", "0.5", "add_value"))),
                upgrade("wind_swiftness", "rare", false, true, List.of(effect("minecraft:speed", "-1", "0"))),
                upgrade("eternal_guardian", "epic", false, true, List.of(command("give @s shield[unbreakable={}] 1"))),
                upgrade("sharpened_edge", "common", true, false, List.of(attribute("roguelikemc:critical_chance", "0.01", "add_value"))),
                upgrade("fatal_precision", "common", true, false, List.of(attribute("roguelikemc:critical_damage", "0.03", "add_value"))),
                upgrade("desperate_strike", "rare", false, true, List.of(
                        attribute("minecraft:generic.attack_damage", "-0.1", "add_multiplied_base"),
                        attribute("roguelikemc:critical_damage", "0.6", "add_value")
                )),
                upgrade("glass_blade", "rare", false, false, List.of(
                        attribute("roguelikemc:critical_chance", "0.15", "add_value"),
                        attribute("roguelikemc:damage_ratio", "0.5", "add_value")
                )),
                upgrade("precision_chain", "rare", false, false, List.of(
                        attribute("roguelikemc:critical_chance", "0.03", "add_value"),
                        attribute("roguelikemc:critical_damage", "0.08", "add_value")
                )),
                upgrade("chilling_aura", "rare", false, true, List.of(event("effect_mobs", "minecraft:slowness", "0", "8.0"))),
                upgrade("cursed_shield", "rare", false, true, List.of(
                        attribute("roguelikemc:damage_ratio", "-0.1", "add_value"),
                        event("effect_mobs", "minecraft:speed", "0", "8.0")
                )),
                upgrade("hardened_instinct", "epic", true, false, List.of(attribute("roguelikemc:damage_ratio", "-0.01", "add_value"))),
                upgrade("fortune_infused", "epic", false, false, List.of(
                        effect("minecraft:luck", "36000", "0"),
                        attribute("roguelikemc:experience_gain", "0.1", "add_value")
                ))
        );

        defaultUpgrades.forEach(upgradeProvider::addUpgrade);
    }

    private static RoguelikeMCUpgradeData upgrade(String id, String tier, boolean isPermanent, boolean isUnique, List<RoguelikeMCUpgradeData.ActionData> actions) {
        return new RoguelikeMCUpgradeData(
                id,
                "upgrade.roguelikemc.name." + id,
                "upgrade.roguelikemc.description." + id,
                tier,
                isPermanent,
                isUnique,
                "roguelikemc:textures/upgrades/" + id + ".png",
                actions
        );
    }

    private static RoguelikeMCUpgradeData.ActionData attribute(String id, String amount, String operation) {
        return action("attribute", id, amount, operation);
    }

    private static RoguelikeMCUpgradeData.ActionData effect(String id, String duration, String amplifier) {
        return action("effect", id, duration, amplifier);
    }

    private static RoguelikeMCUpgradeData.ActionData event(String... value) {
        return action("event", value);
    }

    private static RoguelikeMCUpgradeData.ActionData command(String command) {
        return action("command", command);
    }

    private static RoguelikeMCUpgradeData.ActionData command(String command, boolean needsOwner) {
        return action("command", command, String.valueOf(needsOwner));
    }

    private static RoguelikeMCUpgradeData.ActionData action(String type, String... value) {
        return new RoguelikeMCUpgradeData.ActionData(type, List.of(value));
    }
}
