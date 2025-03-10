package snowball049.roguelikemc;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import snowball049.roguelikemc.config.RoguelikeMCConfig;
import snowball049.roguelikemc.network.packet.RefreshUpgradeOptionC2SPayload;
import snowball049.roguelikemc.network.packet.UpgradeOptionS2CPayload;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RoguelikeMC implements ModInitializer {
	public static final String MOD_ID = "roguelikemc";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		// Init Config Support
		RoguelikeMCConfig.loadConfig();
		RoguelikeMCConfig config = RoguelikeMCConfig.INSTANCE;

		// Handle Network Packet
		PayloadTypeRegistry.playC2S().register(RefreshUpgradeOptionC2SPayload.ID, RefreshUpgradeOptionC2SPayload.CODEC);
		PayloadTypeRegistry.playS2C().register(UpgradeOptionS2CPayload.ID, UpgradeOptionS2CPayload.CODEC);

		// Init Network Handler
        ServerPlayNetworking.registerGlobalReceiver(RefreshUpgradeOptionC2SPayload.ID, (payload, context) -> {
			// Shuffle Upgrade Options and Send to Client
			Collections.shuffle(config.upgrades);
			List<RoguelikeMCConfig.RogueLikeMCUpgradeConfig> currentUpgrades = new ArrayList<>();
			for (int i = 0; i < 3; i++) {
				currentUpgrades.add(config.upgrades.get(i));
			}
			for (RoguelikeMCConfig.RogueLikeMCUpgradeConfig upgrade : currentUpgrades) {
				ServerPlayNetworking.send(context.player(), new UpgradeOptionS2CPayload(upgrade));
			}

		});

		// Command Handler
//		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
//			dispatcher.register(CommandManager.literal("upgrade")
//					.then(CommandManager.argument("tier", StringArgumentType.string())
//							.suggests(RoguelikeMCCommands::suggestUpgrades)
//							.executes(RoguelikeMCCommands::executeCommandUpgrade)
//					)
//			);
//		});

		LOGGER.info("RoguelikeMC Initialized");
	}
}
