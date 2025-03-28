package snowball049.roguelikemc;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityCombatEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.resource.ResourceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import snowball049.roguelikemc.config.RoguelikeMCCommonConfig;
import snowball049.roguelikemc.network.handler.RefreshUpgradeOptionHandler;
import snowball049.roguelikemc.network.handler.SelectUpgradeOptionHandler;
import snowball049.roguelikemc.network.packet.RefreshCurrentUpgradeS2CPayload;
import snowball049.roguelikemc.network.packet.RefreshUpgradeOptionC2SPayload;
import snowball049.roguelikemc.network.packet.SelectUpgradeOptionC2SPayload;
import snowball049.roguelikemc.network.packet.UpgradeOptionS2CPayload;
import snowball049.roguelikemc.upgrade.RoguelikeMCUpgradeManager;
import snowball049.roguelikemc.util.RoguelikeMCRegisterUtil;

public class RoguelikeMC implements ModInitializer {
	public static final String MOD_ID = "roguelikemc";
	public static final Logger LOGGER = LoggerFactory.getLogger("RoguelikeMC");

	@Override
	public void onInitialize() {
		// Init Config Support
		RoguelikeMCCommonConfig.loadConfig();

		// Handle Network Packet
		PayloadTypeRegistry.playC2S().register(RefreshUpgradeOptionC2SPayload.ID, RefreshUpgradeOptionC2SPayload.CODEC);
		PayloadTypeRegistry.playS2C().register(UpgradeOptionS2CPayload.ID, UpgradeOptionS2CPayload.CODEC);
		PayloadTypeRegistry.playC2S().register(SelectUpgradeOptionC2SPayload.ID, SelectUpgradeOptionC2SPayload.CODEC);
		PayloadTypeRegistry.playS2C().register(RefreshCurrentUpgradeS2CPayload.ID, RefreshCurrentUpgradeS2CPayload.CODEC);

		// Init Network Handler
        ServerPlayNetworking.registerGlobalReceiver(RefreshUpgradeOptionC2SPayload.ID, RefreshUpgradeOptionHandler::handle);
		ServerPlayNetworking.registerGlobalReceiver(SelectUpgradeOptionC2SPayload.ID, SelectUpgradeOptionHandler::handle);

		// Join Event
		ServerPlayConnectionEvents.JOIN.register(RoguelikeMCRegisterUtil::onJoinEventRegister);

		// Death Event
		ServerPlayerEvents.COPY_FROM.register(RoguelikeMCRegisterUtil::onDeathEventRegister);

		// Upgrade Point Handler
		ServerEntityCombatEvents.AFTER_KILLED_OTHER_ENTITY.register(RoguelikeMCRegisterUtil::onKillEntityEventRegister);

		// Command Register
		CommandRegistrationCallback.EVENT.register(RoguelikeMCRegisterUtil::commandRegister);

		// Compat Check
		ServerLifecycleEvents.SERVER_STARTED.register(RoguelikeMCRegisterUtil::onServerLoadEventRegister);

		// Datapack Reload
		ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new RoguelikeMCUpgradeManager());

		LOGGER.info("RoguelikeMC Initialized");
	}
}
