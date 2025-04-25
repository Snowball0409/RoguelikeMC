package snowball049.roguelikemc.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.registry.RegistryWrapper;
import java.util.concurrent.CompletableFuture;

public class RoguelikeMCTranslationProvider extends FabricLanguageProvider {
    public RoguelikeMCTranslationProvider(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        // Specifying en_us is optional, as it's the default language code
        super(dataOutput, "en_us", registryLookup);
    }

    @Override
    public void generateTranslations(RegistryWrapper.WrapperLookup wrapperLookup, TranslationBuilder translationBuilder) {
        // Config Screen
        translationBuilder.add("category.roguelikemc.gui", "RoguelikeMC");
        translationBuilder.add("key.roguelikemc.open_gui", "Open Upgrade Screen");

        // Upgrade Screen
        translationBuilder.add("button.roguelikemc.draw_upgrades","Draw Upgrades");
        translationBuilder.add("gui.roguelikemc.upgrade_points", "Upgrade Points: ");
        translationBuilder.add("gui.roguelikemc.temporary_upgrade", "Temporary Upgrades");
        translationBuilder.add("gui.roguelikemc.permanent_upgrade", "Permanent Upgrades");
        translationBuilder.add("gui.roguelikemc.upgrade_pool", "Upgrade Pool");

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
        translationBuilder.add("upgrade.roguelikemc.name.cursed_shield", "Cursed Shield");
        translationBuilder.add("upgrade.roguelikemc.name.chilling_aura", "Chilling Aura");
        translationBuilder.add("upgrade.roguelikemc.name.brutes_strength", "Brute's Strength");
        translationBuilder.add("upgrade.roguelikemc.name.blade_dancer", "Blade Dancer");
        translationBuilder.add("upgrade.roguelikemc.name.berserkers_wrath", "Berserker's Wrath");
        translationBuilder.add("upgrade.roguelikemc.name.adamant_guard", "Adamant Guard");

        // Upgrade Description
        translationBuilder.add("upgrade.roguelikemc.description.withering_aura", "Hostile mobs around you are inflicted with Wither");
        translationBuilder.add("upgrade.roguelikemc.description.wisdoms_bounty", "Experience Gain +6%");
        translationBuilder.add("upgrade.roguelikemc.description.winged_warrior", "Your chestplate becomes an unbreakable and binding Elytra");
        translationBuilder.add("upgrade.roguelikemc.description.wind_swiftness", "Gain Speed I");
        translationBuilder.add("upgrade.roguelikemc.description.villager_slayer", "Killing villagers drops emeralds, but Iron Golems will permanently be hostile");
        translationBuilder.add("upgrade.roguelikemc.description.undying_will", "Retain equipment after death");
        translationBuilder.add("upgrade.roguelikemc.description.turtles_blessing", "Your helmet becomes an unbreakable binding Turtle Helmet");
        translationBuilder.add("upgrade.roguelikemc.description.toxic_presence", "Hostile mobs around you are poisoned");
        translationBuilder.add("upgrade.roguelikemc.description.titans_bulk", "Size doubled, movement speed -20%, attack +20%");
        translationBuilder.add("upgrade.roguelikemc.description.tide_glider", "Water movement speed + 30%");
        translationBuilder.add("upgrade.roguelikemc.description.tank_mode", "Gain Resistance II and +2 max health, but cannot wear pants");
        translationBuilder.add("upgrade.roguelikemc.description.swift_stride", "Movement Speed +4%");
        translationBuilder.add("upgrade.roguelikemc.description.stonebound", "Gain Resistance I, but maximum health -1 heart");
        translationBuilder.add("upgrade.roguelikemc.description.steel_hide", "Armor toughness + 0.5");
        translationBuilder.add("upgrade.roguelikemc.description.skyborn", "Allows creative flight, but maximum health is halved");
        translationBuilder.add("upgrade.roguelikemc.description.sharpened_edge", "Increase Critical Hit Chance by 1%");
        translationBuilder.add("upgrade.roguelikemc.description.scholars_gift", "Receive a Mending book");
        translationBuilder.add("upgrade.roguelikemc.description.prospectors_luck", "Receive some random ores");
        translationBuilder.add("upgrade.roguelikemc.description.precision_chain", "Increase Critical Hit Chance by 3% and Critical Damage by 8%");
        translationBuilder.add("upgrade.roguelikemc.description.pixie_form", "Size halved, movement speed +20%, attack -20%");
        translationBuilder.add("upgrade.roguelikemc.description.one_last_chance", "One-time Totem of Undying");
        translationBuilder.add("upgrade.roguelikemc.description.nocturnal_sight", "Gain night vision");
        translationBuilder.add("upgrade.roguelikemc.description.mystic_steed", "Receive a SPECIAL horse");
        translationBuilder.add("upgrade.roguelikemc.description.miners_frenzy", "Gain Haste");
        translationBuilder.add("upgrade.roguelikemc.description.mighty_force", "Power +5%");
        translationBuilder.add("upgrade.roguelikemc.description.leap_of_faith", "Gain Jump Boost");
        translationBuilder.add("upgrade.roguelikemc.description.last_stand", "Gain Regeneration I");
        translationBuilder.add("upgrade.roguelikemc.description.iron_stance", "Knockback resistance + 1");
        translationBuilder.add("upgrade.roguelikemc.description.infernal_strider", "Wear unbreakable binding strong Netherite Boots");
        translationBuilder.add("upgrade.roguelikemc.description.heavy_appetite", "Gain Saturation, movement speed -50%");
        translationBuilder.add("upgrade.roguelikemc.description.hardened_instinct", "Take 1% less damage");
        translationBuilder.add("upgrade.roguelikemc.description.golden_harvest", "Receive a stack of golden carrots");
        translationBuilder.add("upgrade.roguelikemc.description.golden_fortune", "Receive 16 of golden apple");
        translationBuilder.add("upgrade.roguelikemc.description.gods_fruit", "Receive 1 enchanted golden apple");
        translationBuilder.add("upgrade.roguelikemc.description.glass_blade", "Increase Critical Hit Chance by 15%, Take 50% More Damage");
        translationBuilder.add("upgrade.roguelikemc.description.fortunes_favor", "Luck +1");
        translationBuilder.add("upgrade.roguelikemc.description.fortune_infused", "Gain Luck 30 min and 10% more experience");
        translationBuilder.add("upgrade.roguelikemc.description.feathers_grace", "Gain Slow Falling");
        translationBuilder.add("upgrade.roguelikemc.description.feather_bound", "Immune to fall damage");
        translationBuilder.add("upgrade.roguelikemc.description.fatal_precision", "Increase Critical Damage by 3%");
        translationBuilder.add("upgrade.roguelikemc.description.eternal_guardian", "Gain an unbreakable shield");
        translationBuilder.add("upgrade.roguelikemc.description.eternal_companion", "Receive a tamed cat");
        translationBuilder.add("upgrade.roguelikemc.description.equestrian_gift", "Receive a horse");
        translationBuilder.add("upgrade.roguelikemc.description.enduring_vitality", "Maximum Health + 2");
        translationBuilder.add("upgrade.roguelikemc.description.desperate_strike", "Decrease Base Attack Damage by 10%, Increase Critical Damage by 60%");
        translationBuilder.add("upgrade.roguelikemc.description.deep_lungs", "Breath underwater time + 50%");
        translationBuilder.add("upgrade.roguelikemc.description.cursed_shield", "Take 10% less damage, but nearby hostile mobs gain speed");
        translationBuilder.add("upgrade.roguelikemc.description.chilling_aura", "Hostile mobs around you are slowed");
        translationBuilder.add("upgrade.roguelikemc.description.brutes_strength", "Gain Strength");
        translationBuilder.add("upgrade.roguelikemc.description.blade_dancer", "Attack Speed +5%");
        translationBuilder.add("upgrade.roguelikemc.description.berserkers_wrath", "Strength +50%, but takes 50% more damage");
        translationBuilder.add("upgrade.roguelikemc.description.adamant_guard", "Armor +1");

        // Message
        translationBuilder.add("message.roguelikemc.pass_game_stage", "§aYou have complete the stage: ");
        translationBuilder.add("message.roguelikemc.damage_reduce", "§cYour damage is reduced by %f. You haven't defeated the previous boss!");
        translationBuilder.add("message.roguelikemc.warn_no_upgrade", "§cNo enough upgrades available!");
        translationBuilder.add("message.roguelikemc.drop_equipment", "You have been dropped your equipment!");
    }
}


