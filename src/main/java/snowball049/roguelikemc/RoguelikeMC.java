package snowball049.roguelikemc;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityCombatEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.resource.ResourceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import snowball049.roguelikemc.bootstrap.RoguelikeMCModBootstrap;
import snowball049.roguelikemc.command.RoguelikeMCCommands;
import snowball049.roguelikemc.config.RoguelikeMCCommonConfig;
import snowball049.roguelikemc.network.handler.RefreshUpgradeOptionHandler;
import snowball049.roguelikemc.network.handler.SelectUpgradeOptionHandler;
import snowball049.roguelikemc.network.packet.*;
import snowball049.roguelikemc.upgrade.RoguelikeMCUpgradeManager;
import snowball049.roguelikemc.upgrade.RoguelikeMCUpgradePoolManager;

public class RoguelikeMC implements ModInitializer {
	public static final String MOD_ID = "roguelikemc";
	public static final Logger LOGGER = LoggerFactory.getLogger("RoguelikeMC");

	@Override
	public void onInitialize() {
		RoguelikeMCCommonConfig.loadConfig();

		RoguelikeMCModBootstrap.registerNetworkPackets();
		CommandRegistrationCallback.EVENT.register(RoguelikeMCCommands::register);
		RoguelikeMCModBootstrap.registerAttributes();
		RoguelikeMCModBootstrap.registerItems();
		RoguelikeMCModBootstrap.registerItemGroups();

        ServerPlayNetworking.registerGlobalReceiver(RefreshUpgradeOptionC2SPayload.ID, RefreshUpgradeOptionHandler::handle);
		ServerPlayNetworking.registerGlobalReceiver(SelectUpgradeOptionC2SPayload.ID, SelectUpgradeOptionHandler::handle);

		ServerPlayConnectionEvents.JOIN.register(RoguelikeMCModBootstrap::onPlayerJoin);
		ServerPlayerEvents.COPY_FROM.register(RoguelikeMCModBootstrap::onPlayerDeath);
		ServerEntityCombatEvents.AFTER_KILLED_OTHER_ENTITY.register(RoguelikeMCModBootstrap::onHostileEntityKilled);
		ServerLifecycleEvents.SERVER_STARTED.register(RoguelikeMCModBootstrap::onServerStarted);

		ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new RoguelikeMCUpgradeManager());
		ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new RoguelikeMCUpgradePoolManager());

		ServerTickEvents.END_SERVER_TICK.register(RoguelikeMCModBootstrap::onServerTick);

		LOGGER.info("RoguelikeMC Initialized");
	}
}
