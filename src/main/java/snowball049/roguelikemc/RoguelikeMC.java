package snowball049.roguelikemc;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import snowball049.roguelikemc.config.RoguelikeMCConfig;
import snowball049.roguelikemc.network.handler.RefreshUpgradeOptionHandler;
import snowball049.roguelikemc.network.handler.SelectUpgradeOptionHandler;
import snowball049.roguelikemc.network.packet.RefreshCurrentUpgradeS2CPayload;
import snowball049.roguelikemc.network.packet.RefreshUpgradeOptionC2SPayload;
import snowball049.roguelikemc.network.packet.SelectUpgradeOptionC2SPayload;
import snowball049.roguelikemc.network.packet.UpgradeOptionS2CPayload;
import snowball049.roguelikemc.util.RoguelikeMCUpgradeUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static snowball049.roguelikemc.util.RoguelikeMCUpgradeUtil.*;

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
		PayloadTypeRegistry.playC2S().register(SelectUpgradeOptionC2SPayload.ID, SelectUpgradeOptionC2SPayload.CODEC);
		PayloadTypeRegistry.playS2C().register(RefreshCurrentUpgradeS2CPayload.ID, RefreshCurrentUpgradeS2CPayload.CODEC);

		// Init Network Handler
        ServerPlayNetworking.registerGlobalReceiver(RefreshUpgradeOptionC2SPayload.ID, (payload, context) -> {
			RefreshUpgradeOptionHandler.handle(config, context);
		});
		ServerPlayNetworking.registerGlobalReceiver(SelectUpgradeOptionC2SPayload.ID, SelectUpgradeOptionHandler::handle);

		// Tick Event
//		ServerTickEvents.END_SERVER_TICK.register(server -> {
//			if (server.getTicks() % 20 == 0) {
//				server.getPlayerManager().getPlayerList().forEach(RoguelikeMCUpgradeUtil::applyUpgrade);
//			}
//		});

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
