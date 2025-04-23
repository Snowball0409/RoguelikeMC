package snowball049.roguelikemc.util;

import snowball049.roguelikemc.data.RoguelikeMCUpgradeData;
import snowball049.roguelikemc.datagen.RoguelikeMCUpgradeDataProvider;

import java.util.List;

public class RoguelikeMCDatagenUtil {
    public static void addDefaultUpgrades(RoguelikeMCUpgradeDataProvider upgradeProvider) {
        //permanent upgrades
        final RoguelikeMCUpgradeData fortunes_favor = new RoguelikeMCUpgradeData(
                "fortunes_favor",
                "Fortune's Favor",
                "Luck +1",
                "rare",
                true,
                false,
                "roguelikemc:textures/upgrades/fortunes_favor.png",
                List.of(new RoguelikeMCUpgradeData.ActionData("attribute", List.of("minecraft:generic.luck", "1", "add_value")))
        );

        final RoguelikeMCUpgradeData swift_stride = new RoguelikeMCUpgradeData(
                "swift_stride",
                "Swift Stride",
                "Movement Speed +4%",
                "common",
                true,
                false,
                "roguelikemc:textures/upgrades/swift_stride.png",
                List.of(new RoguelikeMCUpgradeData.ActionData("attribute", List.of("minecraft:generic.movement_speed", "0.04", "add_multiplied_base")))
        );

        final RoguelikeMCUpgradeData enduring_vitality = new RoguelikeMCUpgradeData(
                "enduring_vitality",
                "Enduring Vitality",
                "Maximum Health + 2",
                "common",
                true,
                false,
                "roguelikemc:textures/upgrades/enduring_vitality.png",
                List.of(new RoguelikeMCUpgradeData.ActionData("attribute", List.of("minecraft:generic.max_health", "2", "add_value")))
        );

        final RoguelikeMCUpgradeData blade_dancer = new RoguelikeMCUpgradeData(
                "blade_dancer",
                "Blade Dancer",
                "Attack Speed +5%",
                "common",
                true,
                false,
                "roguelikemc:textures/upgrades/blade_dancer.png",
                List.of(new RoguelikeMCUpgradeData.ActionData("attribute", List.of("minecraft:generic.attack_speed", "0.05", "add_multiplied_base")))
        );

        final RoguelikeMCUpgradeData mighty_force = new RoguelikeMCUpgradeData(
                "mighty_force",
                "Mighty Force",
                "Power +5%",
                "common",
                true,
                false,
                "roguelikemc:textures/upgrades/mighty_force.png",
                List.of(new RoguelikeMCUpgradeData.ActionData("attribute", List.of("minecraft:generic.attack_damage", "0.05", "add_multiplied_base")))
        );

        final RoguelikeMCUpgradeData adamant_guard = new RoguelikeMCUpgradeData(
                "adamant_guard",
                "Adamant Guard",
                "Armor +1",
                "common",
                true,
                false,
                "roguelikemc:textures/upgrades/adamant_guard.png",
                List.of(new RoguelikeMCUpgradeData.ActionData("attribute", List.of("minecraft:generic.armor", "1", "add_value")))
        );

        final RoguelikeMCUpgradeData wisdoms_bounty = new RoguelikeMCUpgradeData(
                "wisdoms_bounty",
                "Wisdom's Bounty",
                "Experience Gain +6%",
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
                "Skyborn",
                "Allows creative flight, but maximum health is halved",
                "legendary",
                false,
                true,
                "roguelikemc:textures/upgrades/skyborn.png",
                List.of(new RoguelikeMCUpgradeData.ActionData("attribute", List.of("minecraft:generic.max_health", "-0.5", "add_multiplied_base")),
                        new RoguelikeMCUpgradeData.ActionData("event", List.of("allow_creative_flying")))
        );

        final RoguelikeMCUpgradeData undying_will = new RoguelikeMCUpgradeData(
                "undying_will",
                "Undying Will",
                "Retain equipment after death",
                "legendary",
                false,
                true,
                "roguelikemc:textures/upgrades/undying_will.png",
                List.of(new RoguelikeMCUpgradeData.ActionData("event", List.of("keep_equipment_after_death")))
        );

        final RoguelikeMCUpgradeData one_last_chance = new RoguelikeMCUpgradeData(
                "one_last_chance",
                "One Last Chance",
                "One-time Totem of Undying",
                "legendary",
                false,
                true,
                "roguelikemc:textures/upgrades/one_last_chance.png",
                List.of(new RoguelikeMCUpgradeData.ActionData("event", List.of("one_last_chance")))
        );

        // Epic tier upgrades
        final RoguelikeMCUpgradeData berserkers_wrath = new RoguelikeMCUpgradeData(
                "berserkers_wrath",
                "Berserker's Wrath",
                "Strength +50%, but takes 50% more damage",
                "epic",
                false,
                false,
                "roguelikemc:textures/upgrades/berserkers_wrath.png",
                List.of(new RoguelikeMCUpgradeData.ActionData("attribute", List.of("minecraft:generic.attack_damage", "0.5", "add_multiplied_base")),
                        new RoguelikeMCUpgradeData.ActionData("attribute", List.of("roguelikemc:damage_ratio", "0.5", "add_value")))
        );

        final RoguelikeMCUpgradeData winged_warrior = new RoguelikeMCUpgradeData(
                "winged_warrior",
                "Winged Warrior",
                "Your chestplate becomes an unbreakable and binding Elytra",
                "epic",
                false,
                true,
                "roguelikemc:textures/upgrades/winged_warrior.png",
                List.of(new RoguelikeMCUpgradeData.ActionData("event", List.of("set_equipment", "2", "{components: {\"minecraft:enchantments\": {levels: {\"minecraft:binding_curse\": 1}}, \"minecraft:unbreakable\": {}}, count: 1, id: \"minecraft:elytra\"}")))
        );

        final RoguelikeMCUpgradeData infernal_strider = new RoguelikeMCUpgradeData(
                "infernal_strider",
                "Infernal Strider",
                "Wear unbreakable binding strong Netherite Boots",
                "epic",
                false,
                true,
                "roguelikemc:textures/upgrades/infernal_strider.png",
                List.of(new RoguelikeMCUpgradeData.ActionData("event", List.of("set_equipment", "0", "{components: {\"minecraft:enchantments\": {levels: {\"minecraft:frost_walker\": 2, \"minecraft:binding_curse\": 1, \"minecraft:protection\": 5}}, \"minecraft:unbreakable\": {}}, count: 1, id: \"minecraft:netherite_boots\"}")))
        );

        final RoguelikeMCUpgradeData heavy_appetite = new RoguelikeMCUpgradeData(
                "heavy_appetite",
                "Heavy Appetite",
                "Gain Saturation, movement speed -50%",
                "epic",
                false,
                true,
                "roguelikemc:textures/upgrades/heavy_appetite.png",
                List.of(new RoguelikeMCUpgradeData.ActionData("effect", List.of("minecraft:saturation", "-1", "0")),
                        new RoguelikeMCUpgradeData.ActionData("attribute", List.of("minecraft:generic.movement_speed", "-0.5", "add_multiplied_base")))
        );

        final RoguelikeMCUpgradeData last_stand = new RoguelikeMCUpgradeData(
                "last_stand",
                "Last Stand",
                "Gain Regeneration I",
                "epic",
                false,
                true,
                "roguelikemc:textures/upgrades/last_stand.png",
                List.of(new RoguelikeMCUpgradeData.ActionData("effect", List.of("minecraft:regeneration", "-1", "0")))
        );

        // Rare tier upgrades
        final RoguelikeMCUpgradeData turtles_blessing = new RoguelikeMCUpgradeData(
                "turtles_blessing",
                "Turtle's Blessing",
                "Your helmet becomes an unbreakable binding Turtle Helmet",
                "rare",
                false,
                true,
                "roguelikemc:textures/upgrades/turtles_blessing.png",
                List.of(new RoguelikeMCUpgradeData.ActionData("event", List.of("set_equipment", "3", "{components: {\"minecraft:enchantments\": {levels: {\"minecraft:binding_curse\": 1}}, \"minecraft:unbreakable\": {}}, count: 1, id: \"minecraft:turtle_helmet\"}")))
        );

        final RoguelikeMCUpgradeData stonebound = new RoguelikeMCUpgradeData(
                "stonebound",
                "Stonebound",
                "Gain Resistance I, but maximum health -1 heart",
                "rare",
                false,
                true,
                "roguelikemc:textures/upgrades/stonebound.png",
                List.of(new RoguelikeMCUpgradeData.ActionData("attribute", List.of("minecraft:generic.max_health", "-2", "add_value")),
                        new RoguelikeMCUpgradeData.ActionData("effect", List.of("minecraft:resistance", "-1", "0")))
        );

        final RoguelikeMCUpgradeData tank_mode = new RoguelikeMCUpgradeData(
                "tank_mode",
                "Tank Mode",
                "Gain Resistance II and +2 max health, but cannot wear pants",
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
                "Villager Slayer",
                "Killing villagers drops emeralds, but Iron Golems will permanently be hostile",
                "rare",
                false,
                true,
                "minecraft:textures/item/emerald.png",
                List.of(new RoguelikeMCUpgradeData.ActionData("event", List.of("add_loot_table", "minecraft:villager", "roguelikemc:villager_slayer")),
                        new RoguelikeMCUpgradeData.ActionData("event", List.of("provoked", "minecraft:iron_golem")))
        );


        final RoguelikeMCUpgradeData mystic_steed = new RoguelikeMCUpgradeData(
                "mystic_steed",
                "Mystic Steed",
                "Receive a SPECIAL horse",
                "rare",
                false,
                false,
                "roguelikemc:textures/upgrades/mystic_steed.png",
                List.of(new RoguelikeMCUpgradeData.ActionData("command", List.of("summon horse ~ ~ ~ {Tame:1b,SaddleItem:{id:\"minecraft:saddle\",count:1},attributes:[{id:\"minecraft:generic.jump_strength\",base:5},{id:\"minecraft:generic.movement_speed\",base:0.05},{id:\"minecraft:generic.fall_damage_multiplier\", base:0}]}")))
        );

        final RoguelikeMCUpgradeData titans_bulk = new RoguelikeMCUpgradeData(
                "titans_bulk",
                "Titan's Bulk",
                "Size doubled, movement speed -20%, attack +20%",
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
                "Pixie Form",
                "Size halved, movement speed +20%, attack -20%",
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
                "Toxic Presence",
                "Hostile mobs around you are poisoned",
                "rare",
                false,
                true,
                "roguelikemc:textures/upgrades/toxic_presence.png",
                List.of(new RoguelikeMCUpgradeData.ActionData("event", List.of("effect_mobs", "minecraft:poison", "0", "8.0")))
        );

        final RoguelikeMCUpgradeData withering_aura = new RoguelikeMCUpgradeData(
                "withering_aura",
                "Withering Aura",
                "Hostile mobs around you are inflicted with Wither",
                "epic",
                false,
                true,
                "roguelikemc:textures/upgrades/withering_aura.png",
                List.of(new RoguelikeMCUpgradeData.ActionData("event", List.of("effect_mobs", "minecraft:wither", "0", "8.0")))
        );

        final RoguelikeMCUpgradeData nocturnal_sight = new RoguelikeMCUpgradeData(
                "nocturnal_sight",
                "Nocturnal Sight",
                "Gain night vision",
                "common",
                false,
                true,
                "roguelikemc:textures/upgrades/nocturnal_sight.png",
                List.of(new RoguelikeMCUpgradeData.ActionData("effect", List.of("minecraft:night_vision", "-1", "0")))
        );

        final RoguelikeMCUpgradeData eternal_companion = new RoguelikeMCUpgradeData(
                "eternal_companion",
                "Eternal Companion",
                "Receive a tamed cat",
                "common",
                false,
                false,
                "roguelikemc:textures/upgrades/eternal_companion.png",
                List.of(new RoguelikeMCUpgradeData.ActionData("command", List.of("summon cat ~ ~ ~ {Invulnerable:1b}")))
        );

        final RoguelikeMCUpgradeData leap_of_faith = new RoguelikeMCUpgradeData(
                "leap_of_faith",
                "Leap of Faith",
                "Gain Jump Boost",
                "common",
                false,
                true,
                "roguelikemc:textures/upgrades/leap_of_faith.png",
                List.of(new RoguelikeMCUpgradeData.ActionData("effect", List.of("minecraft:jump_boost", "-1", "0")))
        );

        final RoguelikeMCUpgradeData feathers_grace = new RoguelikeMCUpgradeData(
                "feathers_grace",
                "Feather's Grace",
                "Gain Slow Falling",
                "common",
                false,
                true,
                "roguelikemc:textures/upgrades/feathers_grace.png",
                List.of(new RoguelikeMCUpgradeData.ActionData("effect", List.of("minecraft:slow_falling", "-1", "0")))
        );

        final RoguelikeMCUpgradeData equestrian_gift = new RoguelikeMCUpgradeData(
                "equestrian_gift",
                "Equestrian Gift",
                "Receive a horse",
                "common",
                false,
                false,
                "roguelikemc:textures/upgrades/equestrian_gift.png",
                List.of(new RoguelikeMCUpgradeData.ActionData("command", List.of("summon horse ~ ~ ~ {Tame:1b,SaddleItem:{id:\"minecraft:saddle\",count:1}}")))
        );

        final RoguelikeMCUpgradeData golden_fortune = new RoguelikeMCUpgradeData(
                "golden_fortune",
                "Golden Fortune",
                "Receive 16 of golden apple",
                "common",
                false,
                false,
                "roguelikemc:textures/upgrades/golden_fortune.png",
                List.of(new RoguelikeMCUpgradeData.ActionData("command", List.of("give @s golden_apple 16")))
        );

        final RoguelikeMCUpgradeData golden_harvest = new RoguelikeMCUpgradeData(
                "golden_harvest",
                "Golden Harvest",
                "Receive a stack of golden carrots",
                "common",
                false,
                false,
                "roguelikemc:textures/upgrades/golden_harvest.png",
                List.of(new RoguelikeMCUpgradeData.ActionData("command", List.of("give @s golden_carrot 64")))
        );

        final RoguelikeMCUpgradeData gods_fruit = new RoguelikeMCUpgradeData(
                "gods_fruit",
                "God's Fruit",
                "Receive 1 enchanted golden apple",
                "common",
                false,
                false,
                "roguelikemc:textures/upgrades/gods_fruit.png",
                List.of(new RoguelikeMCUpgradeData.ActionData("command", List.of("give @s enchanted_golden_apple 1")))
        );

        final RoguelikeMCUpgradeData miners_frenzy = new RoguelikeMCUpgradeData(
                "miners_frenzy",
                "Miner's Frenzy",
                "Gain Haste",
                "common",
                false,
                true,
                "roguelikemc:textures/upgrades/miners_frenzy.png",
                List.of(new RoguelikeMCUpgradeData.ActionData("effect", List.of("minecraft:haste", "-1", "0")))
        );

        final RoguelikeMCUpgradeData scholars_gift = new RoguelikeMCUpgradeData(
                "scholars_gift",
                "Scholar's Gift",
                "Receive a Mending book",
                "common",
                false,
                false,
                "roguelikemc:textures/upgrades/scholars_gift.png",
                List.of(new RoguelikeMCUpgradeData.ActionData("command", List.of("give @p enchanted_book[stored_enchantments={\"minecraft:mending\":1}] 1")))
        );

        final RoguelikeMCUpgradeData prospectors_luck = new RoguelikeMCUpgradeData(
                "prospectors_luck",
                "Prospector's Luck",
                "Receive some random ores",
                "common",
                false,
                false,
                "roguelikemc:textures/upgrades/prospectors_luck.png",
                List.of(new RoguelikeMCUpgradeData.ActionData("command", List.of("loot give @s loot roguelikemc:random_ores")))
        );

        final RoguelikeMCUpgradeData brutes_strength = new RoguelikeMCUpgradeData(
                "brutes_strength",
                "Brute's Strength",
                "Gain Strength",
                "common",
                false,
                true,
                "roguelikemc:textures/upgrades/brutes_strength.png",
                List.of(new RoguelikeMCUpgradeData.ActionData("effect", List.of("minecraft:strength", "-1", "0")))
        );

        final RoguelikeMCUpgradeData feather_bound = new RoguelikeMCUpgradeData(
                "feather_bound",
                "Feather Bound",
                "Immune to fall damage",
                "epic",
                false,
                true,
                "roguelikemc:textures/upgrades/feather_bound.png",
                List.of(new RoguelikeMCUpgradeData.ActionData("attribute", List.of("minecraft:generic.fall_damage_multiplier", "-1", "add_value")))
        );

        final RoguelikeMCUpgradeData tide_glider = new RoguelikeMCUpgradeData(
                "tide_glider",
                "Tide Glider",
                "Water movement speed + 30%",
                "common",
                true,
                false,
                "roguelikemc:textures/upgrades/tide_glider.png",
                List.of(new RoguelikeMCUpgradeData.ActionData("attribute", List.of("minecraft:generic.water_movement_efficiency", "0.33", "add_value")))
        );

        final RoguelikeMCUpgradeData iron_stance = new RoguelikeMCUpgradeData(
                "iron_stance",
                "Iron Stance",
                "Knockback resistance + 1",
                "common",
                true,
                false,
                "roguelikemc:textures/upgrades/iron_stance.png",
                List.of(new RoguelikeMCUpgradeData.ActionData("attribute", List.of("minecraft:generic.knockback_resistance", "1", "add_value")))
        );

        final RoguelikeMCUpgradeData deep_lungs = new RoguelikeMCUpgradeData(
                "deep_lungs",
                "Deep Lungs",
                "Breath underwater time + 50%",
                "common",
                true,
                false,
                "roguelikemc:textures/upgrades/deep_lungs.png",
                List.of(new RoguelikeMCUpgradeData.ActionData("attribute", List.of("minecraft:generic.oxygen_bonus", "1", "add_value")))
        );

        final RoguelikeMCUpgradeData steel_hide = new RoguelikeMCUpgradeData(
                "steel_hide",
                "Steel Hide",
                "Armor toughness + 0.5",
                "common",
                true,
                false,
                "roguelikemc:textures/upgrades/steel_hide.png",
                List.of(new RoguelikeMCUpgradeData.ActionData("attribute", List.of("minecraft:generic.armor_toughness", "0.5", "add_value")))
        );

        final RoguelikeMCUpgradeData wind_swiftness = new RoguelikeMCUpgradeData(
                "wind_swiftness",
                "Wind Swiftness",
                "Gain Speed I",
                "rare",
                false,
                true,
                "roguelikemc:textures/upgrades/wind_swiftness.png",
                List.of(new RoguelikeMCUpgradeData.ActionData("effect", List.of("minecraft:speed", "-1", "0")))
        );

        final RoguelikeMCUpgradeData eternal_guardian = new RoguelikeMCUpgradeData(
                "eternal_guardian",
                "Eternal Guardian",
                "Gain an unbreakable shield",
                "epic",
                false,
                true,
                "roguelikemc:textures/upgrades/eternal_guardian.png",
                List.of(new RoguelikeMCUpgradeData.ActionData("command", List.of("give @s shield[unbreakable={}] 1")))
        );

        final RoguelikeMCUpgradeData sharpened_edge = new RoguelikeMCUpgradeData(
                "sharpened_edge",
                "Sharpened Edge",
                "Increase Critical Hit Chance by 1%",
                "common",
                true,
                false,
                "roguelikemc:textures/upgrades/sharpened_edge.png",
                List.of(new RoguelikeMCUpgradeData.ActionData("attribute", List.of("roguelikemc:critical_chance", "0.01", "add_value")))
        );

        final RoguelikeMCUpgradeData fatal_precision = new RoguelikeMCUpgradeData(
                "fatal_precision",
                "Fatal Precision",
                "Increase Critical Damage by 3%",
                "common",
                true,
                false,
                "roguelikemc:textures/upgrades/fatal_precision.png",
                List.of(new RoguelikeMCUpgradeData.ActionData("attribute", List.of("roguelikemc:critical_damage", "0.03", "add_value")))
        );

        final RoguelikeMCUpgradeData desperate_strike = new RoguelikeMCUpgradeData(
                "desperate_strike",
                "Desperate Strike",
                "Decrease Base Attack Damage by 10%, Increase Critical Damage by 60%",
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
                "Glass Blade",
                "Increase Critical Hit Chance by 15%, Take 50% More Damage",
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
                "Precision Chain",
                "Increase Critical Hit Chance by 3% and Critical Damage by 8%",
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
                "Chilling Aura",
                "Hostile mobs around you are slowed",
                "rare",
                false,
                true,
                "roguelikemc:textures/upgrades/chilling_aura.png",
                List.of(new RoguelikeMCUpgradeData.ActionData("event", List.of("effect_mobs", "minecraft:slowness", "0", "8.0")))
        );

        final RoguelikeMCUpgradeData cursed_shield = new RoguelikeMCUpgradeData(
                "cursed_shield",
                "Cursed Shield",
                "Take 10% less damage, but nearby hostile mobs gain speed",
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
                "Hardened Instinct",
                "Take 1% less damage",
                "epic",
                true,
                false,
                "roguelikemc:textures/upgrades/hardened_instinct.png",
                List.of(new RoguelikeMCUpgradeData.ActionData("attribute", List.of("roguelikemc:damage_ratio", "-0.01", "add_value")))
        );

        final RoguelikeMCUpgradeData fortune_infused = new RoguelikeMCUpgradeData(
                "fortune_infused",
                "Fortune Infused",
                "Gain Luck 30 min and 10% more experience",
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
