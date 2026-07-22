package snowball049.roguelikemc.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;

public class RoguelikeMCTranslationProvider extends FabricLanguageProvider {
    public RoguelikeMCTranslationProvider(FabricDataOutput dataOutput) {
        // Specifying en_us is optional, as it's the default language code
        super(dataOutput, "en_us");
    }

    @Override
    public void generateTranslations(TranslationBuilder translationBuilder) {
        // Config Screen
        translationBuilder.add("category.roguelikemc.gui", "RoguelikeMC");
        translationBuilder.add("key.roguelikemc.open_gui", "Open Upgrade Screen");

        // Upgrade Screen
        translationBuilder.add("button.roguelikemc.draw_upgrades","Draw Upgrades");
        translationBuilder.add("gui.roguelikemc.upgrade_points", "Upgrade Points: ");
        translationBuilder.add("gui.roguelikemc.temporary_upgrade", "Temporary Upgrades");
        translationBuilder.add("gui.roguelikemc.permanent_upgrade", "Permanent Upgrades");
        translationBuilder.add("gui.roguelikemc.upgrade_pool", "Upgrade Pool");
        translationBuilder.add("gui.roguelikemc.next_boss", "Next Boss: ");

        // Items
        translationBuilder.add("item.roguelikemc.upgrade_point_orb", "Orb of Upgrade Point");
        translationBuilder.add("itemGroup.roguelikemc.item_group", "RoguelikeMC");

        // Upgrades name
        translationBuilder.add("upgrade.roguelikemc.name.withering_aura", "Withering Aura");
        translationBuilder.add("upgrade.roguelikemc.name.wisdoms_bounty", "Wisdom's Bounty");
        translationBuilder.add("upgrade.roguelikemc.name.winged_warrior", "Winged Warrior");
        translationBuilder.add("upgrade.roguelikemc.name.wind_swiftness", "Wind Swiftness");
        translationBuilder.add("upgrade.roguelikemc.name.villager_slayer", "Villager Slayer");
        translationBuilder.add("upgrade.roguelikemc.name.undying_will", "Undying Will");
        translationBuilder.add("upgrade.roguelikemc.name.turtles_blessing", "Turtle's Blessing");
        translationBuilder.add("upgrade.roguelikemc.name.toxic_presence", "Toxic Presence");
        translationBuilder.add("upgrade.roguelikemc.name.titans_bulk", "Titan's Bulk");
        translationBuilder.add("upgrade.roguelikemc.name.tide_glider", "Tide Glider");
        translationBuilder.add("upgrade.roguelikemc.name.tank_mode", "Tank Mode");
        translationBuilder.add("upgrade.roguelikemc.name.swift_stride", "Swift Stride");
        translationBuilder.add("upgrade.roguelikemc.name.stonebound", "Stonebound");
        translationBuilder.add("upgrade.roguelikemc.name.steel_hide", "Steel Hide");
        translationBuilder.add("upgrade.roguelikemc.name.skyborn", "Skyborn");
        translationBuilder.add("upgrade.roguelikemc.name.sharpened_edge", "Sharpened Edge");
        translationBuilder.add("upgrade.roguelikemc.name.scholars_gift", "Scholar's Gift");
        translationBuilder.add("upgrade.roguelikemc.name.prospectors_luck", "Prospector's Luck");
        translationBuilder.add("upgrade.roguelikemc.name.precision_chain", "Precision Chain");
        translationBuilder.add("upgrade.roguelikemc.name.predators_momentum", "Predator's Momentum");
        translationBuilder.add("upgrade.roguelikemc.name.pixie_form", "Pixie Form");
        translationBuilder.add("upgrade.roguelikemc.name.one_last_chance", "One Last Chance");
        translationBuilder.add("upgrade.roguelikemc.name.nocturnal_sight", "Nocturnal Sight");
        translationBuilder.add("upgrade.roguelikemc.name.mystic_steed", "Mystic Steed");
        translationBuilder.add("upgrade.roguelikemc.name.miners_frenzy", "Miner's Frenzy");
        translationBuilder.add("upgrade.roguelikemc.name.mighty_force", "Mighty Force");
        translationBuilder.add("upgrade.roguelikemc.name.leap_of_faith", "Leap of Faith");
        translationBuilder.add("upgrade.roguelikemc.name.last_stand", "Last Stand");
        translationBuilder.add("upgrade.roguelikemc.name.iron_stance", "Iron Stance");
        translationBuilder.add("upgrade.roguelikemc.name.infernal_strider", "Infernal Strider");
        translationBuilder.add("upgrade.roguelikemc.name.heavy_appetite", "Heavy Appetite");
        translationBuilder.add("upgrade.roguelikemc.name.hardened_instinct", "Hardened Instinct");
        translationBuilder.add("upgrade.roguelikemc.name.golden_harvest", "Golden Harvest");
        translationBuilder.add("upgrade.roguelikemc.name.golden_fortune", "Golden Fortune");
        translationBuilder.add("upgrade.roguelikemc.name.gods_fruit", "God's Fruit");
        translationBuilder.add("upgrade.roguelikemc.name.glass_blade", "Glass Blade");
        translationBuilder.add("upgrade.roguelikemc.name.fortunes_favor", "Fortune's Favor");
        translationBuilder.add("upgrade.roguelikemc.name.fortune_infused", "Fortune Infused");
        translationBuilder.add("upgrade.roguelikemc.name.feathers_grace", "Feather's Grace");
        translationBuilder.add("upgrade.roguelikemc.name.feather_bound", "Feather Bound");
        translationBuilder.add("upgrade.roguelikemc.name.fatal_precision", "Fatal Precision");
        translationBuilder.add("upgrade.roguelikemc.name.eternal_guardian", "Eternal Guardian");
        translationBuilder.add("upgrade.roguelikemc.name.eternal_companion", "Eternal Companion");
        translationBuilder.add("upgrade.roguelikemc.name.equestrian_gift", "Equestrian Gift");
        translationBuilder.add("upgrade.roguelikemc.name.enduring_vitality", "Enduring Vitality");
        translationBuilder.add("upgrade.roguelikemc.name.desperate_strike", "Desperate Strike");
        translationBuilder.add("upgrade.roguelikemc.name.deep_lungs", "Deep Lungs");
        translationBuilder.add("upgrade.roguelikemc.name.crimson_reckoning", "Crimson Reckoning");
        translationBuilder.add("upgrade.roguelikemc.name.cursed_shield", "Cursed Shield");
        translationBuilder.add("upgrade.roguelikemc.name.chilling_aura", "Chilling Aura");
        translationBuilder.add("upgrade.roguelikemc.name.brutes_strength", "Brute's Strength");
        translationBuilder.add("upgrade.roguelikemc.name.blade_dancer", "Blade Dancer");
        translationBuilder.add("upgrade.roguelikemc.name.berserkers_wrath", "Berserker's Wrath");
        translationBuilder.add("upgrade.roguelikemc.name.adamant_guard", "Adamant Guard");
        translationBuilder.add("upgrade.roguelikemc.name.reapers_bounty", "Reaper's Bounty");
        translationBuilder.add("upgrade.roguelikemc.name.sparring_insight", "Sparring Insight");
        translationBuilder.add("upgrade.roguelikemc.name.bloodrush_strike", "Bloodrush Strike");
        translationBuilder.add("upgrade.roguelikemc.name.repercussion_burst", "Repercussion Burst");
        translationBuilder.add("upgrade.roguelikemc.name.adrenaline_dash", "Adrenaline Dash");
        translationBuilder.add("upgrade.roguelikemc.name.village_haggle", "Village Haggle");
        translationBuilder.add("upgrade.roguelikemc.name.merchant_crown", "Merchant Crown");
        translationBuilder.add("upgrade.roguelikemc.name.ore_roulette", "Ore Roulette");
        translationBuilder.add("upgrade.roguelikemc.name.gilded_spoils", "Gilded Spoils");
        translationBuilder.add("upgrade.roguelikemc.name.levelers_echo", "Leveler's Echo");
        translationBuilder.add("upgrade.roguelikemc.name.ascendant_tempering", "Ascendant Tempering");
        translationBuilder.add("upgrade.roguelikemc.name.witherforged_scrap", "Witherforged Scrap");
        translationBuilder.add("upgrade.roguelikemc.name.goldfang_hoard", "Goldfang Hoard");
        translationBuilder.add("upgrade.roguelikemc.name.voidstone_grudge", "Voidstone Grudge");
        translationBuilder.add("upgrade.roguelikemc.name.revealing_light", "Revealing Light");
        translationBuilder.add("upgrade.roguelikemc.name.enfeebling_aura", "Enfeebling Aura");
        translationBuilder.add("upgrade.roguelikemc.name.gravity_snare", "Gravity Snare");
        translationBuilder.add("upgrade.roguelikemc.name.piglins_favor", "Piglin's Favor");
        translationBuilder.add("upgrade.roguelikemc.name.frostsoul_treads", "Frostsoul Treads");
        translationBuilder.add("upgrade.roguelikemc.name.miasma_ward", "Miasma Ward");
        translationBuilder.add("upgrade.roguelikemc.name.pioneers_cache", "Pioneer's Cache");
        translationBuilder.add("upgrade.roguelikemc.name.excalibur", "Excalibur");
        translationBuilder.add("upgrade.roguelikemc.name.marksmans_codex", "Marksman's Codex");
        translationBuilder.add("upgrade.roguelikemc.name.homestead_flock", "Homestead Flock");
        translationBuilder.add("upgrade.roguelikemc.name.emberheart", "Emberheart");
        translationBuilder.add("upgrade.roguelikemc.name.oceans_blessing", "Ocean's Blessing");

        // Upgrade Description
        translationBuilder.add("upgrade.roguelikemc.description.withering_aura", "Hostile mobs around you are inflicted with Wither");
        translationBuilder.add("upgrade.roguelikemc.description.wisdoms_bounty", "Experience Gain +6%");
        translationBuilder.add("upgrade.roguelikemc.description.winged_warrior", "Your chestplate becomes an unbreakable, bound Elytra");
        translationBuilder.add("upgrade.roguelikemc.description.wind_swiftness", "Gain Speed I");
        translationBuilder.add("upgrade.roguelikemc.description.villager_slayer", "Villagers drop emeralds when killed, but Iron Golems become hostile");
        translationBuilder.add("upgrade.roguelikemc.description.undying_will", "Retain equipment after death");
        translationBuilder.add("upgrade.roguelikemc.description.turtles_blessing", "Your helmet becomes an unbreakable, bound Turtle Helmet");
        translationBuilder.add("upgrade.roguelikemc.description.toxic_presence", "Hostile mobs around you are poisoned");
        translationBuilder.add("upgrade.roguelikemc.description.titans_bulk", "Size +100%, Movement Speed -20%, Attack Damage +20%");
        translationBuilder.add("upgrade.roguelikemc.description.tide_glider", "Water Movement Speed +33%");
        translationBuilder.add("upgrade.roguelikemc.description.tank_mode", "Gain Resistance II, Maximum Health +2 hearts, but cannot wear pants");
        translationBuilder.add("upgrade.roguelikemc.description.swift_stride", "Movement Speed +4%");
        translationBuilder.add("upgrade.roguelikemc.description.stonebound", "Gain Resistance I, but Maximum Health -1 heart");
        translationBuilder.add("upgrade.roguelikemc.description.steel_hide", "Armor Toughness +0.5");
        translationBuilder.add("upgrade.roguelikemc.description.skyborn", "Allows creative flight, but Maximum Health is halved");
        translationBuilder.add("upgrade.roguelikemc.description.sharpened_edge", "Critical Hit Chance +1%");
        translationBuilder.add("upgrade.roguelikemc.description.scholars_gift", "Receive a Mending book");
        translationBuilder.add("upgrade.roguelikemc.description.prospectors_luck", "Receive some random ores");
        translationBuilder.add("upgrade.roguelikemc.description.predators_momentum", "Every 2 kills grant Speed II for 5s, max once per 5s");
        translationBuilder.add("upgrade.roguelikemc.description.precision_chain", "Critical Hit Chance +3%, Critical Damage +8%");
        translationBuilder.add("upgrade.roguelikemc.description.pixie_form", "Size -50%, Movement Speed +20%, Attack Damage -20%");
        translationBuilder.add("upgrade.roguelikemc.description.one_last_chance", "One-time Totem of Undying");
        translationBuilder.add("upgrade.roguelikemc.description.nocturnal_sight", "Gain Night Vision");
        translationBuilder.add("upgrade.roguelikemc.description.mystic_steed", "Receive a special horse");
        translationBuilder.add("upgrade.roguelikemc.description.miners_frenzy", "Gain Haste");
        translationBuilder.add("upgrade.roguelikemc.description.mighty_force", "Attack Damage +5%");
        translationBuilder.add("upgrade.roguelikemc.description.leap_of_faith", "Gain Jump Boost");
        translationBuilder.add("upgrade.roguelikemc.description.last_stand", "Gain Regeneration I");
        translationBuilder.add("upgrade.roguelikemc.description.iron_stance", "Knockback Resistance +1");
        translationBuilder.add("upgrade.roguelikemc.description.infernal_strider", "Receive unbreakable, bound Netherite Boots");
        translationBuilder.add("upgrade.roguelikemc.description.heavy_appetite", "Gain Saturation, but Movement Speed -50%");
        translationBuilder.add("upgrade.roguelikemc.description.hardened_instinct", "Damage Taken -1%");
        translationBuilder.add("upgrade.roguelikemc.description.golden_harvest", "Receive a stack of golden carrots");
        translationBuilder.add("upgrade.roguelikemc.description.golden_fortune", "Receive 16 golden apples");
        translationBuilder.add("upgrade.roguelikemc.description.gods_fruit", "Receive 1 enchanted golden apple");
        translationBuilder.add("upgrade.roguelikemc.description.glass_blade", "Critical Hit Chance +15%, but Damage Taken +50%");
        translationBuilder.add("upgrade.roguelikemc.description.fortunes_favor", "Luck +1");
        translationBuilder.add("upgrade.roguelikemc.description.fortune_infused", "Gain Luck for 30 min, Experience Gain +10%");
        translationBuilder.add("upgrade.roguelikemc.description.feathers_grace", "Gain Slow Falling");
        translationBuilder.add("upgrade.roguelikemc.description.feather_bound", "Immune to fall damage");
        translationBuilder.add("upgrade.roguelikemc.description.fatal_precision", "Critical Damage +3%");
        translationBuilder.add("upgrade.roguelikemc.description.eternal_guardian", "Receive an unbreakable shield");
        translationBuilder.add("upgrade.roguelikemc.description.eternal_companion", "Receive a tamed cat");
        translationBuilder.add("upgrade.roguelikemc.description.equestrian_gift", "Receive a horse");
        translationBuilder.add("upgrade.roguelikemc.description.enduring_vitality", "Maximum Health +1 heart");
        translationBuilder.add("upgrade.roguelikemc.description.desperate_strike", "Attack Damage -10%, but Critical Damage +60%");
        translationBuilder.add("upgrade.roguelikemc.description.deep_lungs", "Underwater Breathing Time +50%");
        translationBuilder.add("upgrade.roguelikemc.description.crimson_reckoning", "Every 3 kills grant Regeneration II for 6s, max once per 8s");
        translationBuilder.add("upgrade.roguelikemc.description.cursed_shield", "Damage Taken -10%, but nearby hostile mobs gain Speed");
        translationBuilder.add("upgrade.roguelikemc.description.chilling_aura", "Hostile mobs around you are slowed");
        translationBuilder.add("upgrade.roguelikemc.description.brutes_strength", "Gain Strength");
        translationBuilder.add("upgrade.roguelikemc.description.blade_dancer", "Attack Speed +5%");
        translationBuilder.add("upgrade.roguelikemc.description.berserkers_wrath", "Attack Damage +50%, but Damage Taken +50%");
        translationBuilder.add("upgrade.roguelikemc.description.adamant_guard", "Armor +1");
        translationBuilder.add("upgrade.roguelikemc.description.reapers_bounty", "Every 5 kills grant 1 golden apple, max once per 20s");
        translationBuilder.add("upgrade.roguelikemc.description.sparring_insight", "Each attack on a hostile mob grants 1 experience point");
        translationBuilder.add("upgrade.roguelikemc.description.bloodrush_strike", "Every 3 hits on hostiles grant Strength II for 5s, max once per 20s");
        translationBuilder.add("upgrade.roguelikemc.description.repercussion_burst", "Every 5 hits taken deal Instant Damage to nearby hostiles");
        translationBuilder.add("upgrade.roguelikemc.description.adrenaline_dash", "Taking damage grants Speed II for 5s, max once per 60s");
        translationBuilder.add("upgrade.roguelikemc.description.village_haggle", "Every 5 trades with villagers/traders grant 1-3 emeralds");
        translationBuilder.add("upgrade.roguelikemc.description.merchant_crown", "Every 20 trades grant a diamond and permanently +1 Luck");
        translationBuilder.add("upgrade.roguelikemc.description.ore_roulette", "Mining overworld ores may drop a random ore; placed ores don't count");
        translationBuilder.add("upgrade.roguelikemc.description.gilded_spoils", "Mining 5 gold ores grants random golden armor; placed ores don't count");
        translationBuilder.add("upgrade.roguelikemc.description.levelers_echo", "Each level gained grants 10 bonus experience points");
        translationBuilder.add("upgrade.roguelikemc.description.ascendant_tempering", "Every 5 levels grant permanent +0.5 health, attack, and armor");
        translationBuilder.add("upgrade.roguelikemc.description.witherforged_scrap", "Wither Skeletons may drop Netherite Scrap");
        translationBuilder.add("upgrade.roguelikemc.description.goldfang_hoard", "Zombified Piglins may drop gold blocks, but become hostile");
        translationBuilder.add("upgrade.roguelikemc.description.voidstone_grudge", "Endermen may drop obsidian, but become hostile");
        translationBuilder.add("upgrade.roguelikemc.description.revealing_light", "Hostile mobs around you are glowing");
        translationBuilder.add("upgrade.roguelikemc.description.enfeebling_aura", "Hostile mobs around you are weakened");
        translationBuilder.add("upgrade.roguelikemc.description.gravity_snare", "Hostile mobs around you are levitated");
        translationBuilder.add("upgrade.roguelikemc.description.piglins_favor", "Your helmet becomes an unbreakable, bound Golden Helmet");
        translationBuilder.add("upgrade.roguelikemc.description.frostsoul_treads", "Your boots become bound Leather Boots, Knockback Resistance +1, Movement Speed +10%");
        translationBuilder.add("upgrade.roguelikemc.description.miasma_ward", "Every 4 hits taken release a Weakness cloud and weaken nearby hostile mobs, max once per 10s");
        translationBuilder.add("upgrade.roguelikemc.description.pioneers_cache", "Receive logs, planks, sticks, torches, apples, and bread");
        translationBuilder.add("upgrade.roguelikemc.description.excalibur", "Receive Excalibur, a stone sword with Netherite-level damage");
        translationBuilder.add("upgrade.roguelikemc.description.marksmans_codex", "Receive an enchanted book: Power V, Unbreaking III, Infinity, Punch II, Flame");
        translationBuilder.add("upgrade.roguelikemc.description.homestead_flock", "Receive 2 each of chickens, pigs, cows, and sheep");
        translationBuilder.add("upgrade.roguelikemc.description.emberheart", "Gain Fire Resistance");
        translationBuilder.add("upgrade.roguelikemc.description.oceans_blessing", "Gain Conduit Power");

        // Message
        translationBuilder.add("message.roguelikemc.pass_game_stage", "§aYou have complete the stage: ");
        translationBuilder.add("message.roguelikemc.damage_reduce", "Your damage is reduced by %s %%. You haven't defeated the previous boss!");
        translationBuilder.add("message.roguelikemc.warn_no_upgrade", "§cNo enough upgrades available!");
        translationBuilder.add("message.roguelikemc.drop_equipment", "You have been dropped your equipment!");
        translationBuilder.add("message.roguelikemc.grant_upgrade_point", "You have been granted %d upgrade points!");
        translationBuilder.add("message.roguelikemc.not_enough_upgrade_point", "You don't have enough upgrade points!");
        translationBuilder.add("message.roguelikemc.boss_not_found", "No next boss found!");
        translationBuilder.add("message.roguelikemc.game_stage_reset", "Due to your death, the game stage has been reset.");
    }
}


