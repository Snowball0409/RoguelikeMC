package snowball049.roguelikemc;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import snowball049.roguelikemc.datagen.RoguelikeMCUpgradeDataProvider;

import java.util.List;

public class RoguelikeMCDataGenerator implements DataGeneratorEntrypoint {

	@Override
	public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
		FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();

		pack.addProvider((FabricDataOutput output) -> {
			RoguelikeMCUpgradeDataProvider upgradeProvider = new RoguelikeMCUpgradeDataProvider(output);

			addDefaultUpgrades(upgradeProvider);

			return upgradeProvider;
		});
	}

	private void addDefaultUpgrades(RoguelikeMCUpgradeDataProvider upgradeProvider) {
		final RoguelikeMCUpgradeDataProvider.RoguelikeMCUpgrade health_1 = new RoguelikeMCUpgradeDataProvider.RoguelikeMCUpgrade(
				"health_1",
				"+1 Health",
				"Add 1 Health",
				"common",
				false,
				false,
				"minecraft:textures/mob_effect/regeneration.png",
				List.of(
						new RoguelikeMCUpgradeDataProvider.RoguelikeMCUpgrade.ActionData("attribute", List.of("minecraft:generic.max_health", "1", "add_value"))
				)
		);
		final RoguelikeMCUpgradeDataProvider.RoguelikeMCUpgrade attack_1 = new RoguelikeMCUpgradeDataProvider.RoguelikeMCUpgrade(
				"attack_1",
				"+1 Attack",
				"Add 1 Attack",
				"common",
				false,
				false,
				"minecraft:textures/mob_effect/strength.png",
				List.of(
						new RoguelikeMCUpgradeDataProvider.RoguelikeMCUpgrade.ActionData("attribute", List.of("minecraft:generic.attack_damage", "1", "add_value"))
				)
		);
		final RoguelikeMCUpgradeDataProvider.RoguelikeMCUpgrade speed_10 = new RoguelikeMCUpgradeDataProvider.RoguelikeMCUpgrade(
				"speed_10",
				"+10% Speed",
				"Add 10% Speed",
				"common",
				true,
				false,
				"minecraft:textures/mob_effect/speed.png",
				List.of(
						new RoguelikeMCUpgradeDataProvider.RoguelikeMCUpgrade.ActionData("attribute", List.of("minecraft:generic.movement_speed", "0.1", "add_multiplied_total"))
				)
		);
		final RoguelikeMCUpgradeDataProvider.RoguelikeMCUpgrade dragon_skin = new RoguelikeMCUpgradeDataProvider.RoguelikeMCUpgrade(
				"dragon_skin",
				"Dragon Skin",
				"Get resistant I but minus 1 Heart",
				"rare",
				false,
				true,
				"minecraft:textures/item/dragon_breath.png",
				List.of(
						new RoguelikeMCUpgradeDataProvider.RoguelikeMCUpgrade.ActionData("attribute", List.of("minecraft:generic.max_health", "-2", "add_value")),
						new RoguelikeMCUpgradeDataProvider.RoguelikeMCUpgrade.ActionData("effect", List.of("minecraft:resistance", "-1", "0"))
				)
		);
		final RoguelikeMCUpgradeDataProvider.RoguelikeMCUpgrade phoenix_feather = new RoguelikeMCUpgradeDataProvider.RoguelikeMCUpgrade(
				"phoenix_feather",
				"Phoenix Feather",
				"Grants regeneration effect and feather falling effect",
				"epic",
				false,
				false,
				"minecraft:textures/item/feather.png",
				List.of(
						new RoguelikeMCUpgradeDataProvider.RoguelikeMCUpgrade.ActionData("effect", List.of("minecraft:regeneration", "-1", "0")),
						new RoguelikeMCUpgradeDataProvider.RoguelikeMCUpgrade.ActionData("effect", List.of("minecraft:slow_falling", "-1", "0"))
				)
		);
		final RoguelikeMCUpgradeDataProvider.RoguelikeMCUpgrade titan_strength = new RoguelikeMCUpgradeDataProvider.RoguelikeMCUpgrade(
				"titan_strength",
				"Titan Strength",
				"Increases attack damage by 5",
				"legendary",
				true,
				false,
				"minecraft:textures/mob_effect/strength.png",
				List.of(
						new RoguelikeMCUpgradeDataProvider.RoguelikeMCUpgrade.ActionData("attribute", List.of("minecraft:generic.attack_damage", "5", "add_value"))
				)
		);

		final RoguelikeMCUpgradeDataProvider.RoguelikeMCUpgrade swift_boots = new RoguelikeMCUpgradeDataProvider.RoguelikeMCUpgrade(
				"swift_boots",
				"Swift Boots",
				"Increases movement speed by 10%",
				"common",
				false,
				true,
				"minecraft:textures/mob_effect/speed.png",
				List.of(
						new RoguelikeMCUpgradeDataProvider.RoguelikeMCUpgrade.ActionData("attribute", List.of("minecraft:generic.movement_speed", "0.1", "add_multiplied_base"))
				)
		);

		final RoguelikeMCUpgradeDataProvider.RoguelikeMCUpgrade iron_hide = new RoguelikeMCUpgradeDataProvider.RoguelikeMCUpgrade(
				"iron_hide",
				"Iron Hide",
				"Increases armor by 3",
				"rare",
				false,
				false,
				"minecraft:textures/item/iron_ingot.png",
				List.of(
						new RoguelikeMCUpgradeDataProvider.RoguelikeMCUpgrade.ActionData("attribute", List.of("minecraft:generic.armor", "3", "add_value"))
				)
		);

		final RoguelikeMCUpgradeDataProvider.RoguelikeMCUpgrade shadow_cloak = new RoguelikeMCUpgradeDataProvider.RoguelikeMCUpgrade(
				"shadow_cloak",
				"Shadow Cloak",
				"Grants invisibility effect for 5 seconds",
				"epic",
				true,
				true,
				"minecraft:textures/mob_effect/invisibility.png",
				List.of(
						new RoguelikeMCUpgradeDataProvider.RoguelikeMCUpgrade.ActionData("effect", List.of("minecraft:invisibility", "-1", "0"))
				)
		);

		final RoguelikeMCUpgradeDataProvider.RoguelikeMCUpgrade fire_touch = new RoguelikeMCUpgradeDataProvider.RoguelikeMCUpgrade(
				"fire_touch",
				"Fire Touch",
				"Sets enemy on fire when hit",
				"rare",
				false,
				false,
				"minecraft:textures/mob_effect/fire_resistance.png",
				List.of(
//                        new RoguelikeMCUpgradeDataProvider.RoguelikeMCUpgrade.ActionData("event", List.of("roguelikemc:set_on_fire", "5", "0"))
				)
		);

		final RoguelikeMCUpgradeDataProvider.RoguelikeMCUpgrade lifesteal = new RoguelikeMCUpgradeDataProvider.RoguelikeMCUpgrade(
				"lifesteal",
				"Life Steal",
				"Heal for 20% of damage dealt",
				"legendary",
				true,
				false,
				"minecraft:textures/mob_effect/health_boost.png",
				List.of(
//                        new RoguelikeMCUpgradeDataProvider.RoguelikeMCUpgrade.ActionData("event", List.of("roguelikemc:lifesteal", "20", "0"))
				)
		);

		final RoguelikeMCUpgradeDataProvider.RoguelikeMCUpgrade arcane_barrier = new RoguelikeMCUpgradeDataProvider.RoguelikeMCUpgrade(
				"arcane_barrier",
				"Arcane Barrier",
				"Grants absorption for 30 seconds",
				"epic",
				false,
				true,
				"minecraft:textures/item/ender_eye.png",
				List.of(
						new RoguelikeMCUpgradeDataProvider.RoguelikeMCUpgrade.ActionData("effect", List.of("minecraft:absorption", "-1", "0"))
				)
		);

		final RoguelikeMCUpgradeDataProvider.RoguelikeMCUpgrade berserker_rage = new RoguelikeMCUpgradeDataProvider.RoguelikeMCUpgrade(
				"berserker_rage",
				"Berserker Rage",
				"Increases attack speed but reduces defense",
				"legendary",
				true,
				true,
				"minecraft:textures/item/golden_sword.png",
				List.of(
						new RoguelikeMCUpgradeDataProvider.RoguelikeMCUpgrade.ActionData("attribute", List.of("minecraft:generic.attack_speed", "0.5", "add_value")),
						new RoguelikeMCUpgradeDataProvider.RoguelikeMCUpgrade.ActionData("attribute", List.of("minecraft:generic.armor", "-2", "add_value"))
				)
		);

		final RoguelikeMCUpgradeDataProvider.RoguelikeMCUpgrade arcane_focus = new RoguelikeMCUpgradeDataProvider.RoguelikeMCUpgrade(
				"arcane_focus",
				"Arcane Focus",
				"Increases mana regeneration by 15%",
				"common",
				false,
				true,
				"minecraft:textures/item/ender_pearl.png",
				List.of(
						new RoguelikeMCUpgradeDataProvider.RoguelikeMCUpgrade.ActionData("attribute", List.of("minecraft:generic.max_health", "1", "add_value")),
						new RoguelikeMCUpgradeDataProvider.RoguelikeMCUpgrade.ActionData("effect", List.of("minecraft:haste", "-1", "0"))
				)
		);

		upgradeProvider.addUpgrade(health_1);
		upgradeProvider.addUpgrade(attack_1);
		upgradeProvider.addUpgrade(speed_10);
		upgradeProvider.addUpgrade(dragon_skin);
		upgradeProvider.addUpgrade(phoenix_feather);
		upgradeProvider.addUpgrade(titan_strength);
		upgradeProvider.addUpgrade(swift_boots);
		upgradeProvider.addUpgrade(iron_hide);
		upgradeProvider.addUpgrade(shadow_cloak);
		upgradeProvider.addUpgrade(fire_touch);
		upgradeProvider.addUpgrade(lifesteal);
		upgradeProvider.addUpgrade(arcane_barrier);
		upgradeProvider.addUpgrade(berserker_rage);
		upgradeProvider.addUpgrade(arcane_focus);
	}
}
