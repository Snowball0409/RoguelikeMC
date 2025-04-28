package snowball049.roguelikemc.util;

import snowball049.roguelikemc.data.RoguelikeMCUpgradeData;
import snowball049.roguelikemc.datagen.RoguelikeMCUpgradeDataProvider;

import java.util.List;

public class RoguelikeMCDatagenUtil {
    public static void addDefaultUpgrades(RoguelikeMCUpgradeDataProvider upgradeProvider) {
        //permanent upgrades
        final RoguelikeMCUpgradeData fortunes_favor = new RoguelikeMCUpgradeData(
                "fortunes_favor",
                "upgrade.roguelikemc.name.fortunes_favor",
                "upgrade.roguelikemc.description.fortunes_favor",
                "rare",
                true,
                false,
                "roguelikemc:textures/upgrades/fortunes_favor.png",
                List.of(new RoguelikeMCUpgradeData.ActionData("attribute", List.of("minecraft:generic.luck", "1", "add_value")))
        );

        final RoguelikeMCUpgradeData swift_stride = new RoguelikeMCUpgradeData(
                "swift_stride",
                "upgrade.roguelikemc.name.swift_stride",
                "upgrade.roguelikemc.description.swift_stride",
                "common",
                true,
                false,
                "roguelikemc:textures/upgrades/swift_stride.png",
                List.of(new RoguelikeMCUpgradeData.ActionData("attribute", List.of("minecraft:generic.movement_speed", "0.04", "add_multiplied_base")))
        );

        final RoguelikeMCUpgradeData enduring_vitality = new RoguelikeMCUpgradeData(
                "enduring_vitality",
                "upgrade.roguelikemc.name.enduring_vitality",
                "upgrade.roguelikemc.description.enduring_vitality",
                "common",
                true,
                false,
                "roguelikemc:textures/upgrades/enduring_vitality.png",
                List.of(new RoguelikeMCUpgradeData.ActionData("attribute", List.of("minecraft:generic.max_health", "2", "add_value")))
        );

        final RoguelikeMCUpgradeData blade_dancer = new RoguelikeMCUpgradeData(
                "blade_dancer",
                "upgrade.roguelikemc.name.blade_dancer",
                "upgrade.roguelikemc.description.blade_dancer",
                "common",
                true,
                false,
                "roguelikemc:textures/upgrades/blade_dancer.png",
                List.of(new RoguelikeMCUpgradeData.ActionData("attribute", List.of("minecraft:generic.attack_speed", "0.05", "add_multiplied_base")))
        );

        final RoguelikeMCUpgradeData mighty_force = new RoguelikeMCUpgradeData(
                "mighty_force",
                "upgrade.roguelikemc.name.mighty_force",
                "upgrade.roguelikemc.description.mighty_force",
                "common",
                true,
                false,
                "roguelikemc:textures/upgrades/mighty_force.png",
                List.of(new RoguelikeMCUpgradeData.ActionData("attribute", List.of("minecraft:generic.attack_damage", "0.05", "add_multiplied_base")))
        );

        final RoguelikeMCUpgradeData adamant_guard = new RoguelikeMCUpgradeData(
                "adamant_guard",
                "upgrade.roguelikemc.name.adamant_guard",
                "upgrade.roguelikemc.description.adamant_guard",
                "common",
                true,
                false,
                "roguelikemc:textures/upgrades/adamant_guard.png",
                List.of(new RoguelikeMCUpgradeData.ActionData("attribute", List.of("minecraft:generic.armor", "1", "add_value")))
        );

        final RoguelikeMCUpgradeData wisdoms_bounty = new RoguelikeMCUpgradeData(
                "wisdoms_bounty",
                "upgrade.roguelikemc.name.wisdoms_bounty",
                "upgrade.roguelikemc.description.wisdoms_bounty",
                "common",
                true,
                false,
                "roguelikemc:textures/upgrades/wisdoms_bounty.png",
                List.of(new RoguelikeMCUpgradeData.ActionData("attribute", List.of("roguelikemc:experience_gain", "0.06", "add_value")))
        );

        //temporary upgrades
        // Legendary tier upgrades
        final RoguelikeMCUpgradeData skyborn = new RoguelikeMCUpgradeData(
                "skyborn",
                "upgrade.roguelikemc.name.skyborn",
                "upgrade.roguelikemc.description.skyborn",
                "legendary",
                false,
                true,
                "roguelikemc:textures/upgrades/skyborn.png",
                List.of(new RoguelikeMCUpgradeData.ActionData("attribute", List.of("minecraft:generic.max_health", "-0.5", "add_multiplied_base")),
                        new RoguelikeMCUpgradeData.ActionData("event", List.of("allow_creative_flying")))
        );

        final RoguelikeMCUpgradeData undying_will = new RoguelikeMCUpgradeData(
                "undying_will",
                "upgrade.roguelikemc.name.undying_will",
                "upgrade.roguelikemc.description.undying_will",
                "legendary",
                false,
                true,
                "roguelikemc:textures/upgrades/undying_will.png",
                List.of(new RoguelikeMCUpgradeData.ActionData("event", List.of("keep_equipment_after_death")))
        );

        final RoguelikeMCUpgradeData one_last_chance = new RoguelikeMCUpgradeData(
                "one_last_chance",
                "upgrade.roguelikemc.name.one_last_chance",
                "upgrade.roguelikemc.description.one_last_chance",
                "legendary",
                false,
                true,
                "roguelikemc:textures/upgrades/one_last_chance.png",
                List.of(new RoguelikeMCUpgradeData.ActionData("event", List.of("one_last_chance")))
        );

        // Epic tier upgrades
        final RoguelikeMCUpgradeData berserkers_wrath = new RoguelikeMCUpgradeData(
                "berserkers_wrath",
                "upgrade.roguelikemc.name.berserkers_wrath",
                "upgrade.roguelikemc.description.berserkers_wrath",
                "epic",
                false,
                false,
                "roguelikemc:textures/upgrades/berserkers_wrath.png",
                List.of(new RoguelikeMCUpgradeData.ActionData("attribute", List.of("minecraft:generic.attack_damage", "0.5", "add_multiplied_base")),
                        new RoguelikeMCUpgradeData.ActionData("attribute", List.of("roguelikemc:damage_ratio", "0.5", "add_value")))
        );

        final RoguelikeMCUpgradeData winged_warrior = new RoguelikeMCUpgradeData(
                "winged_warrior",
                "upgrade.roguelikemc.name.winged_warrior",
                "upgrade.roguelikemc.description.winged_warrior",
                "epic",
                false,
                true,
                "roguelikemc:textures/upgrades/winged_warrior.png",
                List.of(new RoguelikeMCUpgradeData.ActionData("event", List.of("set_equipment", "2", "{components: {\"minecraft:enchantments\": {levels: {\"minecraft:binding_curse\": 1}}, \"minecraft:unbreakable\": {}}, count: 1, id: \"minecraft:elytra\"}")))
        );

        final RoguelikeMCUpgradeData infernal_strider = new RoguelikeMCUpgradeData(
                "infernal_strider",
                "upgrade.roguelikemc.name.infernal_strider",
                "upgrade.roguelikemc.description.infernal_strider",
                "epic",
                false,
                true,
                "roguelikemc:textures/upgrades/infernal_strider.png",
                List.of(new RoguelikeMCUpgradeData.ActionData("event", List.of("set_equipment", "0", "{components: {\"minecraft:enchantments\": {levels: {\"minecraft:frost_walker\": 2, \"minecraft:binding_curse\": 1, \"minecraft:protection\": 5}}, \"minecraft:unbreakable\": {}}, count: 1, id: \"minecraft:netherite_boots\"}")))
        );

        final RoguelikeMCUpgradeData heavy_appetite = new RoguelikeMCUpgradeData(
                "heavy_appetite",
                "upgrade.roguelikemc.name.heavy_appetite",
                "upgrade.roguelikemc.description.heavy_appetite",
                "epic",
                false,
                true,
                "roguelikemc:textures/upgrades/heavy_appetite.png",
                List.of(new RoguelikeMCUpgradeData.ActionData("effect", List.of("minecraft:saturation", "-1", "0")),
                        new RoguelikeMCUpgradeData.ActionData("attribute", List.of("minecraft:generic.movement_speed", "-0.5", "add_multiplied_base")))
        );

        final RoguelikeMCUpgradeData last_stand = new RoguelikeMCUpgradeData(
                "last_stand",
                "upgrade.roguelikemc.name.last_stand",
                "upgrade.roguelikemc.description.last_stand",
                "epic",
                false,
                true,
                "roguelikemc:textures/upgrades/last_stand.png",
                List.of(new RoguelikeMCUpgradeData.ActionData("effect", List.of("minecraft:regeneration", "-1", "0")))
        );

        // Rare tier upgrades
        final RoguelikeMCUpgradeData turtles_blessing = new RoguelikeMCUpgradeData(
                "turtles_blessing",
                "upgrade.roguelikemc.name.turtles_blessing",
                "upgrade.roguelikemc.description.turtles_blessing",
                "rare",
                false,
                true,
                "roguelikemc:textures/upgrades/turtles_blessing.png",
                List.of(new RoguelikeMCUpgradeData.ActionData("event", List.of("set_equipment", "3", "{components: {\"minecraft:enchantments\": {levels: {\"minecraft:binding_curse\": 1}}, \"minecraft:unbreakable\": {}}, count: 1, id: \"minecraft:turtle_helmet\"}")))
        );

        final RoguelikeMCUpgradeData stonebound = new RoguelikeMCUpgradeData(
                "stonebound",
                "upgrade.roguelikemc.name.stonebound",
                "upgrade.roguelikemc.description.stonebound",
                "rare",
                false,
                true,
                "roguelikemc:textures/upgrades/stonebound.png",
                List.of(new RoguelikeMCUpgradeData.ActionData("attribute", List.of("minecraft:generic.max_health", "-2", "add_value")),
                        new RoguelikeMCUpgradeData.ActionData("effect", List.of("minecraft:resistance", "-1", "0")))
        );

        final RoguelikeMCUpgradeData tank_mode = new RoguelikeMCUpgradeData(
                "tank_mode",
                "upgrade.roguelikemc.name.tank_mode",
                "upgrade.roguelikemc.description.tank_mode",
                "rare",
                false,
                true,
                "roguelikemc:textures/upgrades/tank_mode.png",
                List.of(new RoguelikeMCUpgradeData.ActionData("attribute", List.of("minecraft:generic.max_health", "4", "add_value")),
                        new RoguelikeMCUpgradeData.ActionData("effect", List.of("minecraft:resistance", "-1", "1")),
                        new RoguelikeMCUpgradeData.ActionData("event", List.of("set_equipment", "1", "")))
        );

        final RoguelikeMCUpgradeData villager_slayer = new RoguelikeMCUpgradeData(
                "villager_slayer",
                "upgrade.roguelikemc.name.villager_slayer",
                "upgrade.roguelikemc.description.villager_slayer",
                "rare",
                false,
                true,
                "roguelikemc:textures/upgrades/villager_slayer.png",
                List.of(new RoguelikeMCUpgradeData.ActionData("event", List.of("add_loot_table", "minecraft:villager", "roguelikemc:villager_slayer")),
                        new RoguelikeMCUpgradeData.ActionData("event", List.of("provoked", "minecraft:iron_golem")))
        );


        final RoguelikeMCUpgradeData mystic_steed = new RoguelikeMCUpgradeData(
                "mystic_steed",
                "upgrade.roguelikemc.name.mystic_steed",
                "upgrade.roguelikemc.description.mystic_steed",
                "rare",
                false,
                false,
                "roguelikemc:textures/upgrades/mystic_steed.png",
                List.of(new RoguelikeMCUpgradeData.ActionData("command", List.of("summon horse ~ ~ ~ {Tame:1b,SaddleItem:{id:\"minecraft:saddle\",count:1},attributes:[{id:\"minecraft:generic.jump_strength\",base:5},{id:\"minecraft:generic.movement_speed\",base:0.05},{id:\"minecraft:generic.fall_damage_multiplier\", base:0}]}")))
        );

        final RoguelikeMCUpgradeData titans_bulk = new RoguelikeMCUpgradeData(
                "titans_bulk",
                "upgrade.roguelikemc.name.titans_bulk",
                "upgrade.roguelikemc.description.titans_bulk",
                "rare",
                false,
                false,
                "roguelikemc:textures/upgrades/titans_bulk.png",
                List.of(new RoguelikeMCUpgradeData.ActionData("attribute", List.of("minecraft:generic.scale", "1", "add_multiplied_total")),
                        new RoguelikeMCUpgradeData.ActionData("attribute", List.of("minecraft:generic.movement_speed", "-0.2", "add_multiplied_base")),
                        new RoguelikeMCUpgradeData.ActionData("attribute", List.of("minecraft:generic.attack_damage", "0.2", "add_multiplied_base")))
        );

        final RoguelikeMCUpgradeData pixie_form = new RoguelikeMCUpgradeData(
                "pixie_form",
                "upgrade.roguelikemc.name.pixie_form",
                "upgrade.roguelikemc.description.pixie_form",
                "rare",
                false,
                false,
                "roguelikemc:textures/upgrades/pixie_form.png",
                List.of(new RoguelikeMCUpgradeData.ActionData("attribute", List.of("minecraft:generic.scale", "-0.5", "add_multiplied_total")),
                        new RoguelikeMCUpgradeData.ActionData("attribute", List.of("minecraft:generic.movement_speed", "0.2", "add_multiplied_base")),
                        new RoguelikeMCUpgradeData.ActionData("attribute", List.of("minecraft:generic.attack_damage", "-0.2", "add_multiplied_base")))
        );

        // Common tier upgrades
        final RoguelikeMCUpgradeData toxic_presence = new RoguelikeMCUpgradeData(
                "toxic_presence",
                "upgrade.roguelikemc.name.toxic_presence",
                "upgrade.roguelikemc.description.toxic_presence",
                "rare",
                false,
                true,
                "roguelikemc:textures/upgrades/toxic_presence.png",
                List.of(new RoguelikeMCUpgradeData.ActionData("event", List.of("effect_mobs", "minecraft:poison", "0", "8.0")))
        );

        final RoguelikeMCUpgradeData withering_aura = new RoguelikeMCUpgradeData(
                "withering_aura",
                "upgrade.roguelikemc.name.withering_aura",
                "upgrade.roguelikemc.description.withering_aura",
                "epic",
                false,
                true,
                "roguelikemc:textures/upgrades/withering_aura.png",
                List.of(new RoguelikeMCUpgradeData.ActionData("event", List.of("effect_mobs", "minecraft:wither", "0", "8.0")))
        );

        final RoguelikeMCUpgradeData nocturnal_sight = new RoguelikeMCUpgradeData(
                "nocturnal_sight",
                "upgrade.roguelikemc.name.nocturnal_sight",
                "upgrade.roguelikemc.description.nocturnal_sight",
                "common",
                false,
                true,
                "roguelikemc:textures/upgrades/nocturnal_sight.png",
                List.of(new RoguelikeMCUpgradeData.ActionData("effect", List.of("minecraft:night_vision", "-1", "0")))
        );

        final RoguelikeMCUpgradeData eternal_companion = new RoguelikeMCUpgradeData(
                "eternal_companion",
                "upgrade.roguelikemc.name.eternal_companion",
                "upgrade.roguelikemc.description.eternal_companion",
                "common",
                false,
                false,
                "roguelikemc:textures/upgrades/eternal_companion.png",
                List.of(new RoguelikeMCUpgradeData.ActionData("command", List.of("summon cat ~ ~ ~ {Invulnerable:1b}")))
        );

        final RoguelikeMCUpgradeData leap_of_faith = new RoguelikeMCUpgradeData(
                "leap_of_faith",
                "upgrade.roguelikemc.name.leap_of_faith",
                "upgrade.roguelikemc.description.leap_of_faith",
                "common",
                false,
                true,
                "roguelikemc:textures/upgrades/leap_of_faith.png",
                List.of(new RoguelikeMCUpgradeData.ActionData("effect", List.of("minecraft:jump_boost", "-1", "0")))
        );

        final RoguelikeMCUpgradeData feathers_grace = new RoguelikeMCUpgradeData(
                "feathers_grace",
                "upgrade.roguelikemc.name.feathers_grace",
                "upgrade.roguelikemc.description.feathers_grace",
                "common",
                false,
                true,
                "roguelikemc:textures/upgrades/feathers_grace.png",
                List.of(new RoguelikeMCUpgradeData.ActionData("effect", List.of("minecraft:slow_falling", "-1", "0")))
        );

        final RoguelikeMCUpgradeData equestrian_gift = new RoguelikeMCUpgradeData(
                "equestrian_gift",
                "upgrade.roguelikemc.name.equestrian_gift",
                "upgrade.roguelikemc.description.equestrian_gift",
                "common",
                false,
                false,
                "roguelikemc:textures/upgrades/equestrian_gift.png",
                List.of(new RoguelikeMCUpgradeData.ActionData("command", List.of("summon horse ~ ~ ~ {Tame:1b,SaddleItem:{id:\"minecraft:saddle\",count:1}}")))
        );

        final RoguelikeMCUpgradeData golden_fortune = new RoguelikeMCUpgradeData(
                "golden_fortune",
                "upgrade.roguelikemc.name.golden_fortune",
                "upgrade.roguelikemc.description.golden_fortune",
                "common",
                false,
                false,
                "roguelikemc:textures/upgrades/golden_fortune.png",
                List.of(new RoguelikeMCUpgradeData.ActionData("command", List.of("give @s golden_apple 16")))
        );

        final RoguelikeMCUpgradeData golden_harvest = new RoguelikeMCUpgradeData(
                "golden_harvest",
                "upgrade.roguelikemc.name.golden_harvest",
                "upgrade.roguelikemc.description.golden_harvest",
                "common",
                false,
                false,
                "roguelikemc:textures/upgrades/golden_harvest.png",
                List.of(new RoguelikeMCUpgradeData.ActionData("command", List.of("give @s golden_carrot 64")))
        );

        final RoguelikeMCUpgradeData gods_fruit = new RoguelikeMCUpgradeData(
                "gods_fruit",
                "upgrade.roguelikemc.name.gods_fruit",
                "upgrade.roguelikemc.description.gods_fruit",
                "common",
                false,
                false,
                "roguelikemc:textures/upgrades/gods_fruit.png",
                List.of(new RoguelikeMCUpgradeData.ActionData("command", List.of("give @s enchanted_golden_apple 1")))
        );

        final RoguelikeMCUpgradeData miners_frenzy = new RoguelikeMCUpgradeData(
                "miners_frenzy",
                "upgrade.roguelikemc.name.miners_frenzy",
                "upgrade.roguelikemc.description.miners_frenzy",
                "common",
                false,
                true,
                "roguelikemc:textures/upgrades/miners_frenzy.png",
                List.of(new RoguelikeMCUpgradeData.ActionData("effect", List.of("minecraft:haste", "-1", "0")))
        );

        final RoguelikeMCUpgradeData scholars_gift = new RoguelikeMCUpgradeData(
                "scholars_gift",
                "upgrade.roguelikemc.name.scholars_gift",
                "upgrade.roguelikemc.description.scholars_gift",
                "common",
                false,
                false,
                "roguelikemc:textures/upgrades/scholars_gift.png",
                List.of(new RoguelikeMCUpgradeData.ActionData("command", List.of("give @p enchanted_book[stored_enchantments={\"minecraft:mending\":1}] 1")))
        );

        final RoguelikeMCUpgradeData prospectors_luck = new RoguelikeMCUpgradeData(
                "prospectors_luck",
                "upgrade.roguelikemc.name.prospectors_luck",
                "upgrade.roguelikemc.description.prospectors_luck",
                "common",
                false,
                false,
                "roguelikemc:textures/upgrades/prospectors_luck.png",
                List.of(new RoguelikeMCUpgradeData.ActionData("command", List.of("loot give @s loot roguelikemc:random_ores")))
        );

        final RoguelikeMCUpgradeData brutes_strength = new RoguelikeMCUpgradeData(
                "brutes_strength",
                "upgrade.roguelikemc.name.brutes_strength",
                "upgrade.roguelikemc.description.brutes_strength",
                "common",
                false,
                true,
                "roguelikemc:textures/upgrades/brutes_strength.png",
                List.of(new RoguelikeMCUpgradeData.ActionData("effect", List.of("minecraft:strength", "-1", "0")))
        );

        final RoguelikeMCUpgradeData feather_bound = new RoguelikeMCUpgradeData(
                "feather_bound",
                "upgrade.roguelikemc.name.feather_bound",
                "upgrade.roguelikemc.description.feather_bound",
                "epic",
                false,
                true,
                "roguelikemc:textures/upgrades/feather_bound.png",
                List.of(new RoguelikeMCUpgradeData.ActionData("attribute", List.of("minecraft:generic.fall_damage_multiplier", "-1", "add_value")))
        );

        final RoguelikeMCUpgradeData tide_glider = new RoguelikeMCUpgradeData(
                "tide_glider",
                "upgrade.roguelikemc.name.tide_glider",
                "upgrade.roguelikemc.description.tide_glider",
                "common",
                true,
                false,
                "roguelikemc:textures/upgrades/tide_glider.png",
                List.of(new RoguelikeMCUpgradeData.ActionData("attribute", List.of("minecraft:generic.water_movement_efficiency", "0.33", "add_value")))
        );

        final RoguelikeMCUpgradeData iron_stance = new RoguelikeMCUpgradeData(
                "iron_stance",
                "upgrade.roguelikemc.name.iron_stance",
                "upgrade.roguelikemc.description.iron_stance",
                "common",
                true,
                false,
                "roguelikemc:textures/upgrades/iron_stance.png",
                List.of(new RoguelikeMCUpgradeData.ActionData("attribute", List.of("minecraft:generic.knockback_resistance", "0.1", "add_value")))
        );

        final RoguelikeMCUpgradeData deep_lungs = new RoguelikeMCUpgradeData(
                "deep_lungs",
                "upgrade.roguelikemc.name.deep_lungs",
                "upgrade.roguelikemc.description.deep_lungs",
                "common",
                true,
                false,
                "roguelikemc:textures/upgrades/deep_lungs.png",
                List.of(new RoguelikeMCUpgradeData.ActionData("attribute", List.of("minecraft:generic.oxygen_bonus", "1", "add_value")))
        );

        final RoguelikeMCUpgradeData steel_hide = new RoguelikeMCUpgradeData(
                "steel_hide",
                "upgrade.roguelikemc.name.steel_hide",
                "upgrade.roguelikemc.description.steel_hide",
                "common",
                true,
                false,
                "roguelikemc:textures/upgrades/steel_hide.png",
                List.of(new RoguelikeMCUpgradeData.ActionData("attribute", List.of("minecraft:generic.armor_toughness", "0.5", "add_value")))
        );

        final RoguelikeMCUpgradeData wind_swiftness = new RoguelikeMCUpgradeData(
                "wind_swiftness",
                "upgrade.roguelikemc.name.wind_swiftness",
                "upgrade.roguelikemc.description.wind_swiftness",
                "rare",
                false,
                true,
                "roguelikemc:textures/upgrades/wind_swiftness.png",
                List.of(new RoguelikeMCUpgradeData.ActionData("effect", List.of("minecraft:speed", "-1", "0")))
        );

        final RoguelikeMCUpgradeData eternal_guardian = new RoguelikeMCUpgradeData(
                "eternal_guardian",
                "upgrade.roguelikemc.name.eternal_guardian",
                "upgrade.roguelikemc.description.eternal_guardian",
                "epic",
                false,
                true,
                "roguelikemc:textures/upgrades/eternal_guardian.png",
                List.of(new RoguelikeMCUpgradeData.ActionData("command", List.of("give @s shield[unbreakable={}] 1")))
        );

        final RoguelikeMCUpgradeData sharpened_edge = new RoguelikeMCUpgradeData(
                "sharpened_edge",
                "upgrade.roguelikemc.name.sharpened_edge",
                "upgrade.roguelikemc.description.sharpened_edge",
                "common",
                true,
                false,
                "roguelikemc:textures/upgrades/sharpened_edge.png",
                List.of(new RoguelikeMCUpgradeData.ActionData("attribute", List.of("roguelikemc:critical_chance", "0.01", "add_value")))
        );

        final RoguelikeMCUpgradeData fatal_precision = new RoguelikeMCUpgradeData(
                "fatal_precision",
                "upgrade.roguelikemc.name.fatal_precision",
                "upgrade.roguelikemc.description.fatal_precision",
                "common",
                true,
                false,
                "roguelikemc:textures/upgrades/fatal_precision.png",
                List.of(new RoguelikeMCUpgradeData.ActionData("attribute", List.of("roguelikemc:critical_damage", "0.03", "add_value")))
        );

        final RoguelikeMCUpgradeData desperate_strike = new RoguelikeMCUpgradeData(
                "desperate_strike",
                "upgrade.roguelikemc.name.desperate_strike",
                "upgrade.roguelikemc.description.desperate_strike",
                "rare",
                false,
                true,
                "roguelikemc:textures/upgrades/desperate_strike.png",
                List.of(
                        new RoguelikeMCUpgradeData.ActionData("attribute", List.of("minecraft:generic.attack_damage", "-0.1", "add_multiplied_base")),
                        new RoguelikeMCUpgradeData.ActionData("attribute", List.of("roguelikemc:critical_damage", "0.6", "add_value"))
                )
        );

        final RoguelikeMCUpgradeData glass_blade = new RoguelikeMCUpgradeData(
                "glass_blade",
                "upgrade.roguelikemc.name.glass_blade",
                "upgrade.roguelikemc.description.glass_blade",
                "rare",
                false,
                false,
                "roguelikemc:textures/upgrades/glass_blade.png",
                List.of(
                        new RoguelikeMCUpgradeData.ActionData("attribute", List.of("roguelikemc:critical_chance", "0.15", "add_value")),
                        new RoguelikeMCUpgradeData.ActionData("attribute", List.of("roguelikemc:damage_ratio", "0.5", "add_value"))
                )
        );

        final RoguelikeMCUpgradeData precision_chain = new RoguelikeMCUpgradeData(
                "precision_chain",
                "upgrade.roguelikemc.name.precision_chain",
                "upgrade.roguelikemc.description.precision_chain",
                "rare",
                false,
                false,
                "roguelikemc:textures/upgrades/precision_chain.png",
                List.of(
                        new RoguelikeMCUpgradeData.ActionData("attribute", List.of("roguelikemc:critical_chance", "0.03", "add_value")),
                        new RoguelikeMCUpgradeData.ActionData("attribute", List.of("roguelikemc:critical_damage", "0.08", "add_value"))
                )
        );

        final RoguelikeMCUpgradeData chilling_aura = new RoguelikeMCUpgradeData(
                "chilling_aura",
                "upgrade.roguelikemc.name.chilling_aura",
                "upgrade.roguelikemc.description.chilling_aura",
                "rare",
                false,
                true,
                "roguelikemc:textures/upgrades/chilling_aura.png",
                List.of(new RoguelikeMCUpgradeData.ActionData("event", List.of("effect_mobs", "minecraft:slowness", "0", "8.0")))
        );

        final RoguelikeMCUpgradeData cursed_shield = new RoguelikeMCUpgradeData(
                "cursed_shield",
                "upgrade.roguelikemc.name.cursed_shield",
                "upgrade.roguelikemc.description.cursed_shield",
                "rare",
                false,
                true,
                "roguelikemc:textures/upgrades/cursed_shield.png",
                List.of(
                        new RoguelikeMCUpgradeData.ActionData("attribute", List.of("roguelikemc:damage_ratio", "-0.1", "add_value")),
                        new RoguelikeMCUpgradeData.ActionData("event", List.of("effect_mobs", "minecraft:speed", "0", "8.0"))
                )
        );

        final RoguelikeMCUpgradeData hardened_instinct = new RoguelikeMCUpgradeData(
                "hardened_instinct",
                "upgrade.roguelikemc.name.hardened_instinct",
                "upgrade.roguelikemc.description.hardened_instinct",
                "epic",
                true,
                false,
                "roguelikemc:textures/upgrades/hardened_instinct.png",
                List.of(new RoguelikeMCUpgradeData.ActionData("attribute", List.of("roguelikemc:damage_ratio", "-0.01", "add_value")))
        );

        final RoguelikeMCUpgradeData fortune_infused = new RoguelikeMCUpgradeData(
                "fortune_infused",
                "upgrade.roguelikemc.name.fortune_infused",
                "upgrade.roguelikemc.description.fortune_infused",
                "epic",
                false,
                false,
                "roguelikemc:textures/upgrades/fortune_infused.png",
                List.of(
                        new RoguelikeMCUpgradeData.ActionData("effect", List.of("minecraft:luck", "36000", "0")),
                        new RoguelikeMCUpgradeData.ActionData("attribute", List.of("roguelikemc:experience_gain", "0.1", "add_value"))
                )
        );

        upgradeProvider.addUpgrade(fortunes_favor);
        upgradeProvider.addUpgrade(swift_stride);
        upgradeProvider.addUpgrade(enduring_vitality);
        upgradeProvider.addUpgrade(blade_dancer);
        upgradeProvider.addUpgrade(mighty_force);
        upgradeProvider.addUpgrade(adamant_guard);
        upgradeProvider.addUpgrade(skyborn);
        upgradeProvider.addUpgrade(undying_will);
        upgradeProvider.addUpgrade(one_last_chance);
        upgradeProvider.addUpgrade(berserkers_wrath);
        upgradeProvider.addUpgrade(winged_warrior);
        upgradeProvider.addUpgrade(infernal_strider);
        upgradeProvider.addUpgrade(heavy_appetite);
        upgradeProvider.addUpgrade(last_stand);
        upgradeProvider.addUpgrade(turtles_blessing);
        upgradeProvider.addUpgrade(stonebound);
        upgradeProvider.addUpgrade(tank_mode);
        upgradeProvider.addUpgrade(mystic_steed);
        upgradeProvider.addUpgrade(titans_bulk);
        upgradeProvider.addUpgrade(pixie_form);
        upgradeProvider.addUpgrade(toxic_presence);
        upgradeProvider.addUpgrade(withering_aura);
        upgradeProvider.addUpgrade(nocturnal_sight);
        upgradeProvider.addUpgrade(eternal_companion);
        upgradeProvider.addUpgrade(leap_of_faith);
        upgradeProvider.addUpgrade(feathers_grace);
        upgradeProvider.addUpgrade(equestrian_gift);
        upgradeProvider.addUpgrade(golden_fortune);
        upgradeProvider.addUpgrade(golden_harvest);
        upgradeProvider.addUpgrade(gods_fruit);
        upgradeProvider.addUpgrade(miners_frenzy);
        upgradeProvider.addUpgrade(scholars_gift);
        upgradeProvider.addUpgrade(prospectors_luck);
        upgradeProvider.addUpgrade(brutes_strength);
        upgradeProvider.addUpgrade(feather_bound);
        upgradeProvider.addUpgrade(tide_glider);
        upgradeProvider.addUpgrade(iron_stance);
        upgradeProvider.addUpgrade(deep_lungs);
        upgradeProvider.addUpgrade(steel_hide);
        upgradeProvider.addUpgrade(wind_swiftness);
        upgradeProvider.addUpgrade(eternal_guardian);
        upgradeProvider.addUpgrade(wisdoms_bounty);
        upgradeProvider.addUpgrade(sharpened_edge);
        upgradeProvider.addUpgrade(fatal_precision);
        upgradeProvider.addUpgrade(desperate_strike);
        upgradeProvider.addUpgrade(glass_blade);
        upgradeProvider.addUpgrade(precision_chain);
        upgradeProvider.addUpgrade(chilling_aura);
        upgradeProvider.addUpgrade(cursed_shield);
        upgradeProvider.addUpgrade(hardened_instinct);
        upgradeProvider.addUpgrade(fortune_infused);
        upgradeProvider.addUpgrade(villager_slayer);
    }
}