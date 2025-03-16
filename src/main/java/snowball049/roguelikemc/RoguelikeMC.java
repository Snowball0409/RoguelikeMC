package snowball049.roguelikemc;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityCombatEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import snowball049.roguelikemc.config.RoguelikeMCCommonConfig;
import snowball049.roguelikemc.config.RoguelikeMCUpgradesConfig;
import snowball049.roguelikemc.data.RoguelikeMCPlayerData;
import snowball049.roguelikemc.network.handler.RefreshUpgradeOptionHandler;
import snowball049.roguelikemc.network.handler.SelectUpgradeOptionHandler;
import snowball049.roguelikemc.network.packet.RefreshCurrentUpgradeS2CPayload;
import snowball049.roguelikemc.network.packet.RefreshUpgradeOptionC2SPayload;
import snowball049.roguelikemc.network.packet.SelectUpgradeOptionC2SPayload;
import snowball049.roguelikemc.network.packet.UpgradeOptionS2CPayload;
import snowball049.roguelikemc.util.RoguelikeMCUpgradeUtil;

public class RoguelikeMC implements ModInitializer {
	public static final String MOD_ID = "roguelikemc";
	public static final Logger LOGGER = LoggerFactory.getLogger("RoguelikeMC");

	@Override
	public void onInitialize() {
		// Init Config Support
		RoguelikeMCUpgradesConfig.loadConfig();
		RoguelikeMCCommonConfig.loadConfig();
		RoguelikeMCUpgradesConfig upgradeConfig = RoguelikeMCUpgradesConfig.INSTANCE;
		RoguelikeMCCommonConfig commonConfig = RoguelikeMCCommonConfig.INSTANCE;

		// Handle Network Packet
		PayloadTypeRegistry.playC2S().register(RefreshUpgradeOptionC2SPayload.ID, RefreshUpgradeOptionC2SPayload.CODEC);
		PayloadTypeRegistry.playS2C().register(UpgradeOptionS2CPayload.ID, UpgradeOptionS2CPayload.CODEC);
		PayloadTypeRegistry.playC2S().register(SelectUpgradeOptionC2SPayload.ID, SelectUpgradeOptionC2SPayload.CODEC);
		PayloadTypeRegistry.playS2C().register(RefreshCurrentUpgradeS2CPayload.ID, RefreshCurrentUpgradeS2CPayload.CODEC);

		// Init Network Handler
        ServerPlayNetworking.registerGlobalReceiver(RefreshUpgradeOptionC2SPayload.ID, (payload, context) -> {
			if (commonConfig.enableUpgradeSystem) {
				RoguelikeMCPlayerData playerData = RoguelikeMCStateSaverAndLoader.getPlayerState(context.player());
				if(playerData.upgradePoints > 0) {
					playerData.upgradePoints--;
					RefreshUpgradeOptionHandler.handle(upgradeConfig, context);
				}else {
					context.player().sendMessage(Text.literal("You don't have enough upgrade points!"));
				}
			}
		});
		ServerPlayNetworking.registerGlobalReceiver(SelectUpgradeOptionC2SPayload.ID, SelectUpgradeOptionHandler::handle);

		// Tick Event
//		ServerTickEvents.END_SERVER_TICK.register(server -> {
//			if (server.getTicks() % 20 == 0) {
//				server.getPlayerManager().getPlayerList().forEach(RoguelikeMCUpgradeUtil::applyUpgrade);
//			}
//		});
		ServerPlayConnectionEvents.JOIN.register((serverPlayNetworkHandler, packetSender, minecraftServer) ->{
			ServerPlayerEntity player = serverPlayNetworkHandler.getPlayer();
			RoguelikeMCPlayerData playerData = RoguelikeMCStateSaverAndLoader.getPlayerState(player);
			ServerPlayNetworking.send(player, new RefreshCurrentUpgradeS2CPayload(playerData.permanentUpgrades));
			ServerPlayNetworking.send(player, new RefreshCurrentUpgradeS2CPayload(playerData.temporaryUpgrades));
			playerData.permanentUpgrades.forEach(upgrade -> {
				RoguelikeMCUpgradeUtil.applyUpgrade(player, upgrade);
			});
			playerData.temporaryUpgrades.forEach(upgrade -> {
				RoguelikeMCUpgradeUtil.applyUpgrade(player, upgrade);
			});
		});

		ServerPlayerEvents.COPY_FROM.register((oldPlayer, newPlayer, alive) -> {
			RoguelikeMCPlayerData playerData = RoguelikeMCStateSaverAndLoader.getPlayerState(oldPlayer);

			playerData.temporaryUpgrades.clear();
			playerData.permanentUpgrades.forEach(upgrade -> {
				RoguelikeMCUpgradeUtil.applyUpgrade(newPlayer, upgrade);
			});
			ServerPlayNetworking.send(newPlayer, new RefreshCurrentUpgradeS2CPayload(playerData.permanentUpgrades));
			ServerPlayNetworking.send(newPlayer, new RefreshCurrentUpgradeS2CPayload(playerData.temporaryUpgrades));
		});

		// Upgrade Point Handler
		ServerEntityCombatEvents.AFTER_KILLED_OTHER_ENTITY.register((server, entity, context) -> {
			if(commonConfig.enableUpgradeSystem && commonConfig.enableKillHostileEntityUpgrade) {
				if (entity instanceof ServerPlayerEntity) {
					RoguelikeMCPlayerData playerData = RoguelikeMCStateSaverAndLoader.getPlayerState((ServerPlayerEntity) entity);
					playerData.currentKillHostile++;
					if (playerData.currentKillHostile >= playerData.currentKillHostileRequirement) {
						playerData.upgradePoints++;
						playerData.currentKillHostile -= playerData.currentKillHostileRequirement;
						playerData.currentKillHostileRequirement = Math.min(playerData.currentKillHostileRequirement + commonConfig.amountBetweenKillHostileEntityUpgrade, commonConfig.killHostileEntityRequirementMinMax.getLast());
						entity.sendMessage(Text.literal("You got 1 upgrade point!"));
					}
				}
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
