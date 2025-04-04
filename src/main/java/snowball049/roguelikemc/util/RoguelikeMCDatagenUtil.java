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
                "minecraft:textures/item/rabbit_foot.png",
                List.of(new RoguelikeMCUpgradeData.ActionData("attribute", List.of("minecraft:generic.luck", "1", "add_value")))
        );

        final RoguelikeMCUpgradeData swift_stride = new RoguelikeMCUpgradeData(
                "swift_stride",
                "Swift Stride",
                "Movement Speed +4%",
                "common",
                true,
                false,
                "minecraft:textures/item/sugar.png",
                List.of(new RoguelikeMCUpgradeData.ActionData("attribute", List.of("minecraft:generic.movement_speed", "0.04", "add_multiplied_base")))
        );

        final RoguelikeMCUpgradeData enduring_vitality = new RoguelikeMCUpgradeData(
                "enduring_vitality",
                "Enduring Vitality",
                "Maximum Health add a Heart",
                "common",
                true,
                false,
                "minecraft:textures/item/golden_apple.png",
                List.of(new RoguelikeMCUpgradeData.ActionData("attribute", List.of("minecraft:generic.max_health", "2", "add_value")))
        );

        final RoguelikeMCUpgradeData blade_dancer = new RoguelikeMCUpgradeData(
                "blade_dancer",
                "Blade Dancer",
                "Attack Speed +5%",
                "common",
                true,
                false,
                "minecraft:textures/item/iron_sword.png",
                List.of(new RoguelikeMCUpgradeData.ActionData("attribute", List.of("minecraft:generic.attack_speed", "0.05", "add_multiplied_base")))
        );

        final RoguelikeMCUpgradeData mighty_force = new RoguelikeMCUpgradeData(
                "mighty_force",
                "Mighty Force",
                "Power +5%",
                "common",
                true,
                false,
                "minecraft:textures/item/anvil.png",
                List.of(new RoguelikeMCUpgradeData.ActionData("attribute", List.of("minecraft:generic.attack_damage", "0.05", "add_multiplied_base")))
        );

        final RoguelikeMCUpgradeData adamant_guard = new RoguelikeMCUpgradeData(
                "adamant_guard",
                "Adamant Guard",
                "Armor +1",
                "common",
                true,
                false,
                "minecraft:textures/item/iron_chestplate.png",
                List.of(new RoguelikeMCUpgradeData.ActionData("attribute", List.of("minecraft:generic.armor", "1", "add_value")))
        );

        final RoguelikeMCUpgradeData sharpshooters_aim = new RoguelikeMCUpgradeData(
                "sharpshooters_aim",
                "Sharpshooter's Aim",
                "Projectile Power +5%",
                "common",
                true,
                false,
                "minecraft:textures/item/bow.png",
                List.of()
        );

        final RoguelikeMCUpgradeData wisdoms_bounty = new RoguelikeMCUpgradeData(
                "wisdoms_bounty",
                "Wisdom's Bounty",
                "Experience Gain +6%",
                "common",
                true,
                false,
                "minecraft:textures/item/experience_bottle.png",
                List.of()
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
                "minecraft:textures/item/elytra.png",
                List.of()
        );

        final RoguelikeMCUpgradeData undying_will = new RoguelikeMCUpgradeData(
                "undying_will",
                "Undying Will",
                "Retain equipment after death",
                "legendary",
                false,
                true,
                "minecraft:textures/item/totem_of_undying.png",
                List.of()
        );

        final RoguelikeMCUpgradeData one_last_chance = new RoguelikeMCUpgradeData(
                "one_last_chance",
                "One Last Chance",
                "One-time Totem of Undying",
                "legendary",
                false,
                true,
                "minecraft:textures/item/totem_of_undying.png",
                List.of()
        );

        // Epic tier upgrades
        final RoguelikeMCUpgradeData berserkers_wrath = new RoguelikeMCUpgradeData(
                "berserkers_wrath",
                "Berserker's Wrath",
                "Strength +50%, but takes 50% more physical damage",
                "epic",
                false,
                false,
                "minecraft:textures/item/netherite_axe.png",
                List.of()
        );

        final RoguelikeMCUpgradeData glass_cannon = new RoguelikeMCUpgradeData(
                "glass_cannon",
                "Glass Cannon",
                "Projectile damage +50%, but takes 50% more projectile damage",
                "epic",
                false,
                false,
                "minecraft:textures/item/bow.png",
                List.of()
        );

        final RoguelikeMCUpgradeData winged_warrior = new RoguelikeMCUpgradeData(
                "winged_warrior",
                "Winged Warrior",
                "Your chestplate becomes an unbreakable and binding Elytra",
                "epic",
                false,
                true,
                "minecraft:textures/item/elytra.png",
                List.of()
        );

        final RoguelikeMCUpgradeData infernal_strider = new RoguelikeMCUpgradeData(
                "infernal_strider",
                "Infernal Strider",
                "Wear unbreakable binding strong Netherite Boots",
                "epic",
                false,
                true,
                "minecraft:textures/item/netherite_boots.png",
                List.of()
        );

        final RoguelikeMCUpgradeData heavy_appetite = new RoguelikeMCUpgradeData(
                "heavy_appetite",
                "Heavy Appetite",
                "Gain Saturation, movement speed -20%",
                "epic",
                false,
                true,
                "minecraft:textures/item/cooked_beef.png",
                List.of()
        );

        final RoguelikeMCUpgradeData last_stand = new RoguelikeMCUpgradeData(
                "last_stand",
                "Last Stand",
                "When health is below half, gain Regeneration I",
                "epic",
                false,
                false,
                "minecraft:textures/item/golden_apple.png",
                List.of()
        );

        // Rare tier upgrades
        final RoguelikeMCUpgradeData turtles_blessing = new RoguelikeMCUpgradeData(
                "turtles_blessing",
                "Turtle's Blessing",
                "Your helmet becomes an unbreakable binding Turtle Helmet",
                "rare",
                false,
                true,
                "minecraft:textures/item/turtle_helmet.png",
                List.of()
        );

        final RoguelikeMCUpgradeData stonebound = new RoguelikeMCUpgradeData(
                "stonebound",
                "Stonebound",
                "Gain Resistance I, but maximum health -1",
                "rare",
                false,
                false,
                "minecraft:textures/block/stone.png",
                List.of()
        );

        final RoguelikeMCUpgradeData tank_mode = new RoguelikeMCUpgradeData(
                "tank_mode",
                "Tank Mode",
                "Gain Resistance II and +2 max health, but cannot wear pants",
                "rare",
                false,
                true,
                "minecraft:textures/item/iron_chestplate.png",
                List.of()
        );

        final RoguelikeMCUpgradeData villager_slayer = new RoguelikeMCUpgradeData(
                "villager_slayer",
                "Villager Slayer",
                "Killing villagers drops emeralds, but Iron Golems will permanently be hostile",
                "rare",
                false,
                true,
                "minecraft:textures/item/emerald.png",
                List.of()
        );

        final RoguelikeMCUpgradeData spicy_feast = new RoguelikeMCUpgradeData(
                "spicy_feast",
                "Spicy Feast",
                "Eating cooked chicken sets you on fire, but fully restores hunger",
                "rare",
                false,
                true,
                "minecraft:textures/item/cooked_chicken.png",
                List.of()
        );

        final RoguelikeMCUpgradeData mystic_steed = new RoguelikeMCUpgradeData(
                "mystic_steed",
                "Mystic Steed",
                "Receive a SPECIAL horse",
                "rare",
                false,
                false,
                "minecraft:textures/item/golden_horse_armor.png",
                List.of(new RoguelikeMCUpgradeData.ActionData("command", List.of("summon horse ~ ~ ~ {Tame:1b,SaddleItem:{id:\"minecraft:saddle\",count:1},attributes:[{id:\"minecraft:generic.jump_strength\",base:5},{id:\"minecraft:generic.movement_speed\",base:0.05},{id:\"minecraft:generic.fall_damage_multiplier\", base:0}]}")))
        );

        final RoguelikeMCUpgradeData titans_bulk = new RoguelikeMCUpgradeData(
                "titans_bulk",
                "Titan's Bulk",
                "Size doubled, movement speed -20%, attack +20%",
                "rare",
                false,
                false,
                "minecraft:textures/item/enchanted_book.png",
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
                "minecraft:textures/item/feather.png",
                List.of(new RoguelikeMCUpgradeData.ActionData("attribute", List.of("minecraft:generic.scale", "-0.5", "add_multiplied_total")),
                        new RoguelikeMCUpgradeData.ActionData("attribute", List.of("minecraft:generic.movement_speed", "0.2", "add_multiplied_base")),
                        new RoguelikeMCUpgradeData.ActionData("attribute", List.of("minecraft:generic.attack_damage", "-0.2", "add_multiplied_base")))
        );

        // Common tier upgrades
        final RoguelikeMCUpgradeData toxic_presence = new RoguelikeMCUpgradeData(
                "toxic_presence",
                "Toxic Presence",
                "Hostile mobs around you are poisoned",
                "common",
                false,
                true,
                "minecraft:textures/item/spider_eye.png",
                List.of()
        );

        final RoguelikeMCUpgradeData withering_aura = new RoguelikeMCUpgradeData(
                "withering_aura",
                "Withering Aura",
                "Hostile mobs around you are inflicted with Wither",
                "common",
                false,
                true,
                "minecraft:textures/item/wither_rose.png",
                List.of()
        );

        final RoguelikeMCUpgradeData nocturnal_sight = new RoguelikeMCUpgradeData(
                "nocturnal_sight",
                "Nocturnal Sight",
                "Gain night vision",
                "common",
                false,
                true,
                "minecraft:textures/item/ender_eye.png",
                List.of(new RoguelikeMCUpgradeData.ActionData("effect", List.of("minecraft:night_vision", "-1", "0")))
        );

        final RoguelikeMCUpgradeData eternal_companion = new RoguelikeMCUpgradeData(
                "eternal_companion",
                "Eternal Companion",
                "Receive a tamed cat",
                "common",
                false,
                false,
                "minecraft:textures/item/cod.png",
                List.of(new RoguelikeMCUpgradeData.ActionData("command", List.of("summon cat ~ ~ ~ {Invulnerable:1b}")))
        );

        final RoguelikeMCUpgradeData grass_grazer = new RoguelikeMCUpgradeData(
                "grass_grazer",
                "Grass Grazer",
                "Sneaking allows you to eat grass",
                "common",
                false,
                true,
                "minecraft:textures/block/grass_block_top.png",
                List.of()
        );

        final RoguelikeMCUpgradeData leap_of_faith = new RoguelikeMCUpgradeData(
                "leap_of_faith",
                "Leap of Faith",
                "Gain Jump Boost",
                "common",
                false,
                false,
                "minecraft:textures/item/rabbit_foot.png",
                List.of(new RoguelikeMCUpgradeData.ActionData("effect", List.of("minecraft:jump_boost", "-1", "0")))
        );

        final RoguelikeMCUpgradeData feathers_grace = new RoguelikeMCUpgradeData(
                "feathers_grace",
                "Feather's Grace",
                "Gain Slow Falling",
                "common",
                false,
                false,
                "minecraft:textures/item/feather.png",
                List.of(new RoguelikeMCUpgradeData.ActionData("effect", List.of("minecraft:slow_falling", "-1", "0")))
        );

        final RoguelikeMCUpgradeData equestrian_gift = new RoguelikeMCUpgradeData(
                "equestrian_gift",
                "Equestrian Gift",
                "Receive a horse",
                "common",
                false,
                false,
                "minecraft:textures/item/saddle.png",
                List.of(new RoguelikeMCUpgradeData.ActionData("command", List.of("summon horse ~ ~ ~ {Tame:1b,SaddleItem:{id:\"minecraft:saddle\",count:1}}")))
        );

        final RoguelikeMCUpgradeData golden_fortune = new RoguelikeMCUpgradeData(
                "golden_fortune",
                "Golden Fortune",
                "Receive half stack of golden apple",
                "common",
                false,
                false,
                "minecraft:textures/item/golden_apple.png",
                List.of(new RoguelikeMCUpgradeData.ActionData("command", List.of("give @s golden_apple 32")))
        );

        final RoguelikeMCUpgradeData golden_harvest = new RoguelikeMCUpgradeData(
                "golden_harvest",
                "Golden Harvest",
                "Receive a stack of golden carrots",
                "common",
                false,
                false,
                "minecraft:textures/item/golden_carrot.png",
                List.of(new RoguelikeMCUpgradeData.ActionData("command", List.of("give @s golden_carrot 64")))
        );

        final RoguelikeMCUpgradeData gods_fruit = new RoguelikeMCUpgradeData(
                "gods_fruit",
                "God's Fruit",
                "Receive 1 enchanted golden apple",
                "common",
                false,
                false,
                "minecraft:textures/item/enchanted_golden_apple.png",
                List.of(new RoguelikeMCUpgradeData.ActionData("command", List.of("give @s enchanted_golden_apple 1")))
        );

        final RoguelikeMCUpgradeData miners_frenzy = new RoguelikeMCUpgradeData(
                "miners_frenzy",
                "Miner's Frenzy",
                "Gain Haste",
                "common",
                false,
                false,
                "minecraft:textures/item/golden_pickaxe.png",
                List.of(new RoguelikeMCUpgradeData.ActionData("effect", List.of("minecraft:haste", "-1", "0")))
        );

        final RoguelikeMCUpgradeData scholars_gift = new RoguelikeMCUpgradeData(
                "scholars_gift",
                "Scholar's Gift",
                "Receive a Mending book",
                "common",
                false,
                false,
                "minecraft:textures/item/book.png",
                List.of(new RoguelikeMCUpgradeData.ActionData("command", List.of("give @p enchanted_book[stored_enchantments={\"minecraft:mending\":1}] 1")))
        );

        final RoguelikeMCUpgradeData prospectors_luck = new RoguelikeMCUpgradeData(
                "prospectors_luck",
                "Prospector's Luck",
                "Receive a stack of random ores",
                "common",
                false,
                false,
                "minecraft:textures/item/diamond_ore.png",
                List.of()
        );

        final RoguelikeMCUpgradeData brutes_strength = new RoguelikeMCUpgradeData(
                "brutes_strength",
                "Brute's Strength",
                "Gain Strength",
                "common",
                false,
                false,
                "minecraft:textures/item/netherite_sword.png",
                List.of(new RoguelikeMCUpgradeData.ActionData("effect", List.of("minecraft:strength", "-1", "0")))
        );

        upgradeProvider.addUpgrade(fortunes_favor);
        upgradeProvider.addUpgrade(swift_stride);
        upgradeProvider.addUpgrade(enduring_vitality);
        upgradeProvider.addUpgrade(blade_dancer);
        upgradeProvider.addUpgrade(mighty_force);
        upgradeProvider.addUpgrade(adamant_guard);
        upgradeProvider.addUpgrade(sharpshooters_aim);
        upgradeProvider.addUpgrade(wisdoms_bounty);
        upgradeProvider.addUpgrade(skyborn);
        upgradeProvider.addUpgrade(undying_will);
        upgradeProvider.addUpgrade(one_last_chance);
        upgradeProvider.addUpgrade(berserkers_wrath);
        upgradeProvider.addUpgrade(glass_cannon);
        upgradeProvider.addUpgrade(winged_warrior);
        upgradeProvider.addUpgrade(infernal_strider);
        upgradeProvider.addUpgrade(heavy_appetite);
        upgradeProvider.addUpgrade(last_stand);
        upgradeProvider.addUpgrade(turtles_blessing);
        upgradeProvider.addUpgrade(stonebound);
        upgradeProvider.addUpgrade(tank_mode);
        upgradeProvider.addUpgrade(villager_slayer);
        upgradeProvider.addUpgrade(spicy_feast);
        upgradeProvider.addUpgrade(mystic_steed);
        upgradeProvider.addUpgrade(titans_bulk);
        upgradeProvider.addUpgrade(pixie_form);
        upgradeProvider.addUpgrade(toxic_presence);
        upgradeProvider.addUpgrade(withering_aura);
        upgradeProvider.addUpgrade(nocturnal_sight);
        upgradeProvider.addUpgrade(eternal_companion);
        upgradeProvider.addUpgrade(grass_grazer);
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
    }
}
