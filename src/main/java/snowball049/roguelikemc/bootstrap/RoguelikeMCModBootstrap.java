package snowball049.roguelikemc.bootstrap;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import snowball049.roguelikemc.RoguelikeMC;
import snowball049.roguelikemc.RoguelikeMCStateSaverAndLoader;
import snowball049.roguelikemc.compat.RoguelikeMCCompat;
import snowball049.roguelikemc.config.RoguelikeMCCommonConfig;
import snowball049.roguelikemc.data.RoguelikeMCAttribute;
import snowball049.roguelikemc.data.RoguelikeMCPlayerData;
import snowball049.roguelikemc.data.RoguelikeMCUpgradeData;
import snowball049.roguelikemc.item.RoguelikeMCItemGroup;
import snowball049.roguelikemc.item.RoguelikeMCItems;
import snowball049.roguelikemc.network.packet.*;
import snowball049.roguelikemc.upgrade.apply.UpgradeApplier;
import snowball049.roguelikemc.upgrade.point.UpgradePointService;
import snowball049.roguelikemc.upgrade.tick.UpgradeTickService;

public final class RoguelikeMCModBootstrap {
    private RoguelikeMCModBootstrap() {
    }

    public static void registerNetworkPackets() {
        PayloadTypeRegistry.playC2S().register(RefreshUpgradeOptionC2SPayload.ID, RefreshUpgradeOptionC2SPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(UpgradeOptionS2CPayload.ID, UpgradeOptionS2CPayload.CODEC);
        PayloadTypeRegistry.playC2S().register(SelectUpgradeOptionC2SPayload.ID, SelectUpgradeOptionC2SPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(RefreshCurrentUpgradeS2CPayload.ID, RefreshCurrentUpgradeS2CPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(SendUpgradePointsS2CPayload.ID, SendUpgradePointsS2CPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(RefreshCurrentBossStageS2CPayload.ID, RefreshCurrentBossStageS2CPayload.CODEC);
    }

    public static void registerItems() {
        RoguelikeMCItems.initailize();
    }

    public static void registerItemGroups() {
        Registry.register(Registries.ITEM_GROUP, Identifier.tryParse(RoguelikeMC.MOD_ID, "item_group"), RoguelikeMCItemGroup.INSTANCE);
    }

    public static void registerAttributes() {
        FabricDefaultAttributeRegistry.register(EntityType.PLAYER, PlayerEntity.createPlayerAttributes()
                .add(RoguelikeMCAttribute.EXPERIENCE_GAIN)
                .add(RoguelikeMCAttribute.DAMAGE_RATIO)
                .add(RoguelikeMCAttribute.CRITICAL_CHANCE)
                .add(RoguelikeMCAttribute.CRITICAL_DAMAGE)
                .add(RoguelikeMCAttribute.THORNS_DAMAGE));
    }

    public static void onPlayerDeath(ServerPlayerEntity oldPlayer, ServerPlayerEntity newPlayer, boolean alive) {
        if (alive) return;
        RoguelikeMCPlayerData playerData = RoguelikeMCStateSaverAndLoader.getPlayerState(oldPlayer);

        playerData.reset();
        playerData.temporaryUpgradeIds.clear();
        playerData.getPermanentUpgrades().forEach(upgrade -> UpgradeApplier.applyUpgrade(newPlayer, upgrade));

        if (playerData.keepEquipmentAfterDeath) {
            for (int i = 0; i < oldPlayer.getInventory().armor.size(); i++) {
                newPlayer.getInventory().armor.set(i, oldPlayer.getInventory().armor.get(i));
            }
            oldPlayer.getInventory().armor.clear();
            oldPlayer.getInventory().dropAll();
        }

        UpgradeApplier.syncOwnedUpgrades(newPlayer, playerData);
        if (RoguelikeMCCommonConfig.INSTANCE.enableLinearGameStage) {
            ServerPlayNetworking.send(newPlayer, new RefreshCurrentBossStageS2CPayload(RoguelikeMCCommonConfig.INSTANCE.gameStageEntities.get(playerData.currentGameStage)));
            newPlayer.sendMessage(Text.translatable("message.roguelikemc.game_stage_reset").formatted(Formatting.RED));
        }
    }

    public static void onPlayerJoin(ServerPlayNetworkHandler serverPlayNetworkHandler, PacketSender ignoredPacketSender, MinecraftServer ignoredServer) {
        ServerPlayerEntity player = serverPlayNetworkHandler.getPlayer();
        RoguelikeMCPlayerData playerData = RoguelikeMCStateSaverAndLoader.getPlayerState(player);

        if (RoguelikeMCCommonConfig.INSTANCE.enableUpgradeSystem) {
            UpgradeApplier.syncOwnedUpgrades(player, playerData);
            for (RoguelikeMCUpgradeData upgrade : playerData.getCurrentOptions()) {
                ServerPlayNetworking.send(player, new UpgradeOptionS2CPayload(upgrade));
            }
            playerData.getPermanentUpgrades().forEach(upgrade -> UpgradeApplier.applyJoinUpgrade(player, upgrade));
            playerData.getTemporaryUpgrades().forEach(upgrade -> UpgradeApplier.applyJoinUpgrade(player, upgrade));
            ServerPlayNetworking.send(player, new SendUpgradePointsS2CPayload(playerData.upgradePoints));
        }

        if (RoguelikeMCCommonConfig.INSTANCE.enableLinearGameStage && playerData.currentGameStage < RoguelikeMCCommonConfig.INSTANCE.gameStageEntities.size()) {
            ServerPlayNetworking.send(player, new RefreshCurrentBossStageS2CPayload(RoguelikeMCCommonConfig.INSTANCE.gameStageEntities.get(playerData.currentGameStage)));
            RoguelikeMC.LOGGER.info(String.valueOf(playerData.currentGameStage));
        } else {
            ServerPlayNetworking.send(player, new RefreshCurrentBossStageS2CPayload("none"));
        }
    }

    public static void onServerStarted(MinecraftServer ignoredServer) {
        RoguelikeMCCompat.load();
    }

    public static void onHostileEntityKilled(ServerWorld ignoredServer, Entity entity, LivingEntity context) {
        if (!RoguelikeMCCommonConfig.INSTANCE.enableUpgradeSystem || !RoguelikeMCCommonConfig.INSTANCE.enableKillHostileEntityUpgrade) {
            return;
        }

        if (entity instanceof ServerPlayerEntity player && context instanceof HostileEntity && !context.isPlayer()) {
            RoguelikeMCPlayerData playerData = RoguelikeMCStateSaverAndLoader.getPlayerState(player);
            UpgradePointService.KillProgressResult progress = UpgradePointService.applyKillProgress(
                    playerData.currentKillHostile,
                    1,
                    RoguelikeMCCommonConfig.INSTANCE.killHostileEntityRequirement
            );
            playerData.currentKillHostile = progress.remainingKills();
            for (int i = 0; i < progress.pointsEarned(); i++) {
                UpgradePointService.addUpgradePoints(player, 1);
            }
        }
    }

    public static void onServerTick(MinecraftServer minecraftServer) {
        if (minecraftServer.getTicks() % 40 == 0) {
            UpgradeTickService.tickInfiniteEffects(minecraftServer);
        }
        if (minecraftServer.getTicks() % 20 == 0) {
            UpgradeTickService.tickEvents(minecraftServer, 20);
        }
        if (minecraftServer.getTicks() % 100 == 0) {
            UpgradeTickService.tickEvents(minecraftServer, 100);
        }
    }
}
