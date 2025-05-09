package snowball049.roguelikemc.util;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.IdentifierArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import snowball049.roguelikemc.RoguelikeMC;
import snowball049.roguelikemc.RoguelikeMCStateSaverAndLoader;
import snowball049.roguelikemc.command.RoguelikeMCCommands;
import snowball049.roguelikemc.compat.RoguelikeMCCompat;
import snowball049.roguelikemc.config.RoguelikeMCCommonConfig;
import snowball049.roguelikemc.data.RoguelikeMCAttribute;
import snowball049.roguelikemc.data.RoguelikeMCPlayerData;
import snowball049.roguelikemc.data.RoguelikeMCUpgradeData;
import snowball049.roguelikemc.item.RoguelikeMCItemGroup;
import snowball049.roguelikemc.item.RoguelikeMCItems;
import snowball049.roguelikemc.network.packet.*;

public class RoguelikeMCRegisterUtil {

    public static void networkPacketRegister() {
        // Register network packets here
        PayloadTypeRegistry.playC2S().register(RefreshUpgradeOptionC2SPayload.ID, RefreshUpgradeOptionC2SPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(UpgradeOptionS2CPayload.ID, UpgradeOptionS2CPayload.CODEC);
        PayloadTypeRegistry.playC2S().register(SelectUpgradeOptionC2SPayload.ID, SelectUpgradeOptionC2SPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(RefreshCurrentUpgradeS2CPayload.ID, RefreshCurrentUpgradeS2CPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(SendUpgradePointsS2CPayload.ID, SendUpgradePointsS2CPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(RefreshCurrentBossStageS2CPayload.ID, RefreshCurrentBossStageS2CPayload.CODEC);
    }

    public static void ItemRegister() {
        // Register items here
        RoguelikeMCItems.initailize();
    }

    public static void ItemGroupRegister() {
        // Register item groups here
        Registry.register(Registries.ITEM_GROUP, Identifier.tryParse(RoguelikeMC.MOD_ID, "item_group"), RoguelikeMCItemGroup.INSTANCE);
    }

    public static void onDeathEventRegister(ServerPlayerEntity oldPlayer, ServerPlayerEntity newPlayer, boolean alive) {
        if (alive) return;
        RoguelikeMCPlayerData playerData = RoguelikeMCStateSaverAndLoader.getPlayerState(oldPlayer);

        playerData.reset();

        playerData.temporaryUpgrades.clear();
        playerData.permanentUpgrades.forEach(upgrade -> RoguelikeMCUpgradeUtil.applyUpgrade(newPlayer, upgrade));

        if(playerData.keepEquipmentAfterDeath){
            for (int i = 0; i < oldPlayer.getInventory().armor.size(); i++) {
                newPlayer.getInventory().armor.set(i, oldPlayer.getInventory().armor.get(i));
            }
            oldPlayer.getInventory().armor.clear();
            oldPlayer.getInventory().dropAll();
        }

        ServerPlayNetworking.send(newPlayer, new RefreshCurrentUpgradeS2CPayload(true, playerData.permanentUpgrades));
        ServerPlayNetworking.send(newPlayer, new RefreshCurrentUpgradeS2CPayload(false, playerData.temporaryUpgrades));
        if (RoguelikeMCCommonConfig.INSTANCE.enableLinearGameStage){
            ServerPlayNetworking.send(newPlayer, new RefreshCurrentBossStageS2CPayload(RoguelikeMCCommonConfig.INSTANCE.gameStageEntities.get(playerData.currentGameStage)));
            newPlayer.sendMessage(Text.translatable("message.roguelikemc.game_stage_reset").formatted(Formatting.RED));
        }
    }

    public static void onJoinEventRegister(ServerPlayNetworkHandler serverPlayNetworkHandler, PacketSender packetSender, MinecraftServer minecraftServer) {
        ServerPlayerEntity player = serverPlayNetworkHandler.getPlayer();
        RoguelikeMCPlayerData playerData = RoguelikeMCStateSaverAndLoader.getPlayerState(player);

        if (RoguelikeMCCommonConfig.INSTANCE.enableUpgradeSystem) {
            // Send upgrade to client
            ServerPlayNetworking.send(player, new RefreshCurrentUpgradeS2CPayload(true, playerData.permanentUpgrades));
            ServerPlayNetworking.send(player, new RefreshCurrentUpgradeS2CPayload(false, playerData.temporaryUpgrades));
            for (RoguelikeMCUpgradeData upgrade : playerData.currentOptions) {
                ServerPlayNetworking.send(player, new UpgradeOptionS2CPayload(upgrade));
            }
            playerData.permanentUpgrades.forEach(upgrade -> RoguelikeMCUpgradeUtil.applyJoinUpgrade(player, upgrade));
            playerData.temporaryUpgrades.forEach(upgrade -> RoguelikeMCUpgradeUtil.applyJoinUpgrade(player, upgrade));

            // Send upgrade points to client
            ServerPlayNetworking.send(player, new SendUpgradePointsS2CPayload(playerData.upgradePoints));
        }

        // Send boss stage to client
        if (RoguelikeMCCommonConfig.INSTANCE.enableLinearGameStage) {
            ServerPlayNetworking.send(player, new RefreshCurrentBossStageS2CPayload(RoguelikeMCCommonConfig.INSTANCE.gameStageEntities.get(playerData.currentGameStage)));
            RoguelikeMC.LOGGER.info(String.valueOf(playerData.currentGameStage));
        }
        else {
            ServerPlayNetworking.send(player, new RefreshCurrentBossStageS2CPayload("none"));
        }
    }

    public static void onServerLoadEventRegister(MinecraftServer minecraftServer) {
        RoguelikeMCCompat.load();
    }

    public static void commandRegister(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment){
        dispatcher.register(CommandManager.literal("roguelikemc")
                .then(CommandManager.argument("player", EntityArgumentType.players())
                        .then(CommandManager.literal("upgrade")
                                .then(CommandManager.literal("grant")
                                        .then(CommandManager.argument("upgradeOption", IdentifierArgumentType.identifier()).suggests(new RoguelikeMCCommands.UpgradeSuggestionProvider())
                                                .executes(RoguelikeMCCommands::grantUpgrade)
                                        )
                                )
                                .then(CommandManager.literal("remove")
                                        .then(CommandManager.argument("upgradeOption", IdentifierArgumentType.identifier()).suggests(new RoguelikeMCCommands.UpgradeSuggestionProvider())
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
                                .then(CommandManager.literal("set")
                                        .then(CommandManager.argument("amount", IntegerArgumentType.integer(1))
                                                .executes(RoguelikeMCCommands::setPoint)
                                        )
                                )
                                .then(CommandManager.literal("get")
                                        .executes(RoguelikeMCCommands::getPoint))
                        )
                        .then(CommandManager.literal("upgrade_pool")
                                .then(CommandManager.literal("add")
                                    .then(CommandManager.argument("upgradePoolOption", IdentifierArgumentType.identifier()).suggests(new RoguelikeMCCommands.UpgradePoolSuggestionProvider())
                                                .executes(RoguelikeMCCommands::addUpgradePool))
                                )
                                .then(CommandManager.literal("remove")
                                        .then(CommandManager.argument("upgradePoolOption", IdentifierArgumentType.identifier()).suggests(new RoguelikeMCCommands.UpgradePoolSuggestionProvider())
                                                .executes(RoguelikeMCCommands::removeUpgradePool))
                                )
                                .then(CommandManager.literal("get")
                                        .executes(RoguelikeMCCommands::getUpgradePool))
                                )
                        )
                );
    }

    public static void onKillEntityEventRegister(ServerWorld server, Entity entity, LivingEntity context) {
        if(RoguelikeMCCommonConfig.INSTANCE.enableUpgradeSystem && RoguelikeMCCommonConfig.INSTANCE.enableKillHostileEntityUpgrade) {
            if (entity instanceof ServerPlayerEntity && context instanceof HostileEntity && !context.isPlayer()) {
                RoguelikeMCPlayerData playerData = RoguelikeMCStateSaverAndLoader.getPlayerState((ServerPlayerEntity) entity);
                playerData.currentKillHostile++;
                while (playerData.currentKillHostile >= RoguelikeMCCommonConfig.INSTANCE.killHostileEntityRequirement) {
                    playerData.currentKillHostile -= RoguelikeMCCommonConfig.INSTANCE.killHostileEntityRequirement;
                    RoguelikeMCPointUtil.addUpgradePoints((ServerPlayerEntity) entity, 1);
                }
            }
        }
    }

    public static void onServerTick(MinecraftServer minecraftServer) {
        // Handle infinite effect upgrade every 2 seconds
        if (minecraftServer.getTicks() % 40 == 0) {
            RoguelikeMCUpgradeUtil.tickInfiniteEffects(minecraftServer);
        }
        // Handle empty set_equipment upgrade every second
        if (minecraftServer.getTicks() % 20 == 0) {
            RoguelikeMCUpgradeUtil.tickSetEquipment(minecraftServer);
            RoguelikeMCUpgradeUtil.tickEffectToMobEntity(minecraftServer);
            RoguelikeMCUpgradeUtil.tickEnableCreativeFly(minecraftServer);
        }
        //Handle particle effect every 5 seconds
        if (minecraftServer.getTicks() % 100 == 0) {
            RoguelikeMCEventUtil.tickPlayerEvent(minecraftServer);
        }
    }

    public static void AttributeRegister() {
        // Register attribute to player
        FabricDefaultAttributeRegistry.register(EntityType.PLAYER, PlayerEntity.createPlayerAttributes()
                .add(RoguelikeMCAttribute.EXPERIENCE_GAIN)
                .add(RoguelikeMCAttribute.DAMAGE_RATIO)
                .add(RoguelikeMCAttribute.CRITICAL_CHANCE)
                .add(RoguelikeMCAttribute.CRITICAL_DAMAGE));
    }
}
