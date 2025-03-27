package snowball049.roguelikemc;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.data.server.advancement.AdvancementProvider;
import snowball049.roguelikemc.datagen.RoguelikeMCUpgradeDataProvider;

public class RoguelikeMCDataGenerator implements DataGeneratorEntrypoint {

	@Override
	public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
		FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();

		pack.addProvider((FabricDataOutput output) -> {
			RoguelikeMCUpgradeDataProvider upgradeProvider = new RoguelikeMCUpgradeDataProvider(output);

			upgradeProvider.addUpgrade(
					new RoguelikeMCUpgradeDataProvider.UpgradeData("health_1", "+1 Health")
							.description("Add 1 Health")
							.tier("common")
							.isPermanent(false)
							.isUnique(false)
							.icon("minecraft:textures/mob_effect/regeneration.png")
							.addEffectAction("attribute", "minecraft:generic.max_health", "1", "add_value")
			);

			upgradeProvider.addUpgrade(
					new RoguelikeMCUpgradeDataProvider.UpgradeData("attack_1", "+1 Attack")
							.description("Add 1 Attack")
							.tier("common")
							.isPermanent(false)
							.isUnique(false)
							.icon("minecraft:textures/mob_effect/strength.png")
							.addEffectAction("attribute", "minecraft:generic.attack_damage", "1", "add_value")
			);

			upgradeProvider.addUpgrade(
					new RoguelikeMCUpgradeDataProvider.UpgradeData("speed_10", "+10% Speed")
							.description("Add 10% Speed")
							.tier("common")
							.isPermanent(true)
							.isUnique(false)
							.icon("minecraft:textures/mob_effect/speed.png")
							.addEffectAction("attribute", "minecraft:generic.movement_speed", "0.1", "add_multiplied_total")
			);

			upgradeProvider.addUpgrade(
					new RoguelikeMCUpgradeDataProvider.UpgradeData("dragon_skin", "Dragon Skin")
							.description("Get resistant I but minus 1 Heart")
							.tier("rare")
							.isPermanent(false)
							.isUnique(true)
							.icon("minecraft:textures/item/dragon_breath.png")
							.addEffectAction("attribute", "minecraft:generic.max_health", "-2", "add_value")
							.addEffectAction("effect", "minecraft:resistance", "-1", "0")
			);

			upgradeProvider.addUpgrade(
					new RoguelikeMCUpgradeDataProvider.UpgradeData("phoenix_feather", "Phoenix Feather")
							.description("Grants regeneration effect and feather falling effect")
							.tier("epic")
							.isPermanent(false)
							.isUnique(false)
							.icon("minecraft:textures/item/feather.png")
							.addEffectAction("effect", "minecraft:regeneration", "-1", "0")
							.addEffectAction("effect", "minecraft:slow_falling", "-1", "0")
			);

			upgradeProvider.addUpgrade(
					new RoguelikeMCUpgradeDataProvider.UpgradeData("titan_strength", "Titan Strength")
							.description("Increases attack damage by 5")
							.tier("legendary")
							.isPermanent(true)
							.isUnique(false)
							.icon("minecraft:textures/mob_effect/strength.png")
							.addEffectAction("attribute", "minecraft:generic.attack_damage", "5", "add_value")
			);

			upgradeProvider.addUpgrade(
					new RoguelikeMCUpgradeDataProvider.UpgradeData("swift_boots", "Swift Boots")
							.description("Increases movement speed by 10%")
							.tier("common")
							.isPermanent(false)
							.isUnique(true)
							.icon("minecraft:textures/mob_effect/speed.png")
							.addEffectAction("attribute", "minecraft:generic.movement_speed", "0.1", "add_multiplied_base")
			);

			upgradeProvider.addUpgrade(
					new RoguelikeMCUpgradeDataProvider.UpgradeData("iron_hide", "Iron Hide")
							.description("Increases armor by 3")
							.tier("rare")
							.isPermanent(false)
							.isUnique(false)
							.icon("minecraft:textures/item/iron_ingot.png")
							.addEffectAction("attribute", "minecraft:generic.armor", "3", "add_value")
			);

			upgradeProvider.addUpgrade(
					new RoguelikeMCUpgradeDataProvider.UpgradeData("shadow_cloak", "Shadow Cloak")
							.description("Grants invisibility effect for 5 seconds")
							.tier("epic")
							.isPermanent(true)
							.isUnique(true)
							.icon("minecraft:textures/mob_effect/invisibility.png")
							.addEffectAction("effect", "minecraft:invisibility", "-1", "0")
			);

			// Note: Commented out event actions are left out
			upgradeProvider.addUpgrade(
					new RoguelikeMCUpgradeDataProvider.UpgradeData("arcane_barrier", "Arcane Barrier")
							.description("Grants absorption for 30 seconds")
							.tier("epic")
							.isPermanent(false)
							.isUnique(true)
							.icon("minecraft:textures/item/ender_eye.png")
							.addEffectAction("effect", "minecraft:absorption", "-1", "0")
			);

			upgradeProvider.addUpgrade(
					new RoguelikeMCUpgradeDataProvider.UpgradeData("berserker_rage", "Berserker Rage")
							.description("Increases attack speed but reduces defense")
							.tier("legendary")
							.isPermanent(true)
							.isUnique(true)
							.icon("minecraft:textures/item/golden_sword.png")
							.addEffectAction("attribute", "minecraft:generic.attack_speed", "0.5", "add_value")
							.addEffectAction("attribute", "minecraft:generic.armor", "-2", "add_value")
			);

			upgradeProvider.addUpgrade(
					new RoguelikeMCUpgradeDataProvider.UpgradeData("arcane_focus", "Arcane Focus")
							.description("Increases mana regeneration by 15%")
							.tier("common")
							.isPermanent(false)
							.isUnique(true)
							.icon("minecraft:textures/item/ender_pearl.png")
							.addEffectAction("attribute", "minecraft:generic.max_health", "1", "add_value")
							.addEffectAction("effect", "minecraft:haste", "-1", "0")
			);

			return upgradeProvider;
		});
	}
}
