package snowball049.roguelikemc;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import snowball049.roguelikemc.datagen.RoguelikeMCUpgradeDataProvider;
import snowball049.roguelikemc.util.RoguelikeMCDatagenUtil;

public class RoguelikeMCDataGenerator implements DataGeneratorEntrypoint {

	@Override
	public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
		FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();

		pack.addProvider((FabricDataOutput output) -> {
			RoguelikeMCUpgradeDataProvider upgradeProvider = new RoguelikeMCUpgradeDataProvider(output);

			RoguelikeMCDatagenUtil.addDefaultUpgrades(upgradeProvider);

			return upgradeProvider;
		});
	}
}
