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
import snowball049.roguelikemc.config.RoguelikeMCCommonConfig;
import snowball049.roguelikemc.network.handler.RefreshUpgradeOptionHandler;
import snowball049.roguelikemc.network.handler.SelectUpgradeOptionHandler;
import snowball049.roguelikemc.network.packet.*;
import snowball049.roguelikemc.upgrade.RoguelikeMCUpgradeManager;
import snowball049.roguelikemc.util.RoguelikeMCRegisterUtil;

public class RoguelikeMC implements ModInitializer {
	public static final String MOD_ID = "roguelikemc";
	public static final Logger LOGGER = LoggerFactory.getLogger("RoguelikeMC");

	@Override
	public void onInitialize() {
		// Init Config Support
		RoguelikeMCCommonConfig.loadConfig();

		// Network Packet Register
		RoguelikeMCRegisterUtil.networkPacketRegister();

		// Command Register
		CommandRegistrationCallback.EVENT.register(RoguelikeMCRegisterUtil::commandRegister);

		// Attribute Register
		RoguelikeMCRegisterUtil.AttributeRegister();

		// Item Register
		RoguelikeMCRegisterUtil.ItemRegister();

		// Item Group Register
		RoguelikeMCRegisterUtil.ItemGroupRegister();

		// Init Network Handler
        ServerPlayNetworking.registerGlobalReceiver(RefreshUpgradeOptionC2SPayload.ID, RefreshUpgradeOptionHandler::handle);
		ServerPlayNetworking.registerGlobalReceiver(SelectUpgradeOptionC2SPayload.ID, SelectUpgradeOptionHandler::handle);

		// Join Event
		ServerPlayConnectionEvents.JOIN.register(RoguelikeMCRegisterUtil::onJoinEventRegister);

		// Death Event
		ServerPlayerEvents.COPY_FROM.register(RoguelikeMCRegisterUtil::onDeathEventRegister);

		// Upgrade Point Handler
		ServerEntityCombatEvents.AFTER_KILLED_OTHER_ENTITY.register(RoguelikeMCRegisterUtil::onKillEntityEventRegister);

		// Compat Check
		ServerLifecycleEvents.SERVER_STARTED.register(RoguelikeMCRegisterUtil::onServerLoadEventRegister);

		// Datapack Reload
		ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new RoguelikeMCUpgradeManager());

		// Server tick event
		ServerTickEvents.END_SERVER_TICK.register(RoguelikeMCRegisterUtil::onServerTick);

		LOGGER.info("RoguelikeMC Initialized");
	}
}
