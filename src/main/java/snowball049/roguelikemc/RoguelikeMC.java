package snowball049.roguelikemc;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityCombatEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.resource.ResourceType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import snowball049.roguelikemc.command.RoguelikeMCCommands;
import snowball049.roguelikemc.config.RoguelikeMCCommonConfig;
import snowball049.roguelikemc.data.RoguelikeMCPlayerData;
import snowball049.roguelikemc.network.handler.RefreshUpgradeOptionHandler;
import snowball049.roguelikemc.network.handler.SelectUpgradeOptionHandler;
import snowball049.roguelikemc.network.packet.RefreshCurrentUpgradeS2CPayload;
import snowball049.roguelikemc.network.packet.RefreshUpgradeOptionC2SPayload;
import snowball049.roguelikemc.network.packet.SelectUpgradeOptionC2SPayload;
import snowball049.roguelikemc.network.packet.UpgradeOptionS2CPayload;
import snowball049.roguelikemc.upgrade.RoguelikeMCUpgradeManager;
import snowball049.roguelikemc.util.RoguelikeMCUpgradeUtil;
import snowball049.roguelikemc.compat.RoguelikeMCCompat;

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
        ServerPlayNetworking.registerGlobalReceiver(RefreshUpgradeOptionC2SPayload.ID, (payload, context) -> {
			if (RoguelikeMCCommonConfig.INSTANCE.enableUpgradeSystem) {
				RoguelikeMCPlayerData playerData = RoguelikeMCStateSaverAndLoader.getPlayerState(context.player());
				if(playerData.upgradePoints > 0) {
					playerData.upgradePoints--;
					RefreshUpgradeOptionHandler.handle(context);
				}else {
					context.player().sendMessage(Text.literal("You don't have enough upgrade points!"));
				}
			}
		});
		ServerPlayNetworking.registerGlobalReceiver(SelectUpgradeOptionC2SPayload.ID, SelectUpgradeOptionHandler::handle);

		// Join Event
		ServerPlayConnectionEvents.JOIN.register((serverPlayNetworkHandler, packetSender, minecraftServer) ->{
			ServerPlayerEntity player = serverPlayNetworkHandler.getPlayer();
			RoguelikeMCPlayerData playerData = RoguelikeMCStateSaverAndLoader.getPlayerState(player);
			ServerPlayNetworking.send(player, new RefreshCurrentUpgradeS2CPayload(playerData.permanentUpgrades));
			ServerPlayNetworking.send(player, new RefreshCurrentUpgradeS2CPayload(playerData.temporaryUpgrades));
			playerData.permanentUpgrades.forEach(upgrade -> RoguelikeMCUpgradeUtil.applyUpgrade(player, upgrade));
			playerData.temporaryUpgrades.forEach(upgrade -> RoguelikeMCUpgradeUtil.applyUpgrade(player, upgrade));
		});

		// Death Event
		ServerPlayerEvents.COPY_FROM.register((oldPlayer, newPlayer, alive) -> {
			RoguelikeMCPlayerData playerData = RoguelikeMCStateSaverAndLoader.getPlayerState(oldPlayer);

			playerData.temporaryUpgrades.clear();
			playerData.permanentUpgrades.forEach(upgrade -> RoguelikeMCUpgradeUtil.applyUpgrade(newPlayer, upgrade));

			playerData.currentKillHostile = 0;
			playerData.currentLevelGain = 0;
			playerData.currentKillHostileRequirement = RoguelikeMCCommonConfig.INSTANCE.killHostileEntityRequirementMinMax.getFirst();

			ServerPlayNetworking.send(newPlayer, new RefreshCurrentUpgradeS2CPayload(playerData.permanentUpgrades));
			ServerPlayNetworking.send(newPlayer, new RefreshCurrentUpgradeS2CPayload(playerData.temporaryUpgrades));
		});

		// Upgrade Point Handler
		ServerEntityCombatEvents.AFTER_KILLED_OTHER_ENTITY.register((server, entity, context) -> {
			if(RoguelikeMCCommonConfig.INSTANCE.enableUpgradeSystem && RoguelikeMCCommonConfig.INSTANCE.enableKillHostileEntityUpgrade) {
				if (entity instanceof ServerPlayerEntity && context.isMobOrPlayer() && !context.isPlayer()) {
					RoguelikeMCPlayerData playerData = RoguelikeMCStateSaverAndLoader.getPlayerState((ServerPlayerEntity) entity);
					playerData.currentKillHostile++;
					while (playerData.currentKillHostile >= playerData.currentKillHostileRequirement) {
						playerData.upgradePoints++;
						playerData.currentKillHostile -= playerData.currentKillHostileRequirement;
						playerData.currentKillHostileRequirement = Math.min(
								playerData.currentKillHostileRequirement + RoguelikeMCCommonConfig.INSTANCE.amountBetweenKillHostileEntityUpgrade,
								RoguelikeMCCommonConfig.INSTANCE.killHostileEntityRequirementMinMax.getLast()
						);
						RoguelikeMCUpgradeUtil.sendPointMessage((ServerPlayerEntity) entity, 1);
					}
				}
			}
		});

		// Command Register
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
			dispatcher.register(CommandManager.literal("roguelikemc")
					.then(CommandManager.argument("player", EntityArgumentType.players())
							.then(CommandManager.literal("upgrade")
								.then(CommandManager.literal("grant")
									.then(CommandManager.argument("upgradeOption", StringArgumentType.string()).suggests(new RoguelikeMCCommands.UpgradeSuggestionProvider())
											.executes(RoguelikeMCCommands::grantUpgrade)
									)
								)
								.then(CommandManager.literal("remove")
									.then(CommandManager.argument("upgradeOption", StringArgumentType.string()).suggests(new RoguelikeMCCommands.UpgradeSuggestionProvider())
											.executes(RoguelikeMCCommands::removeUpgrade)
									)
								)
								.then(CommandManager.literal("clear")
									.executes(RoguelikeMCCommands::clearUpgrade)
								)
							)
							.then(CommandManager.literal("point")
								.then(CommandManager.literal("add")
										.then(CommandManager.argument("amount", IntegerArgumentType.integer(1))
												.executes(RoguelikeMCCommands::addPoint)
										)
								)
								.then(CommandManager.literal("remove")
										.then(CommandManager.argument("amount", IntegerArgumentType.integer(1))
												.executes(RoguelikeMCCommands::removePoint)
										)
								)
							)
					)
			);
		});

		// Compat Check when
		ServerLifecycleEvents.SERVER_STARTED.register(server -> {
			RoguelikeMCCompat.load();
		});

		ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new RoguelikeMCUpgradeManager());

		LOGGER.info("RoguelikeMC Initialized");
	}
}
