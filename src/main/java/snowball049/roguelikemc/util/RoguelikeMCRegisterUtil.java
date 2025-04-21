package snowball049.roguelikemc.util;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.IdentifierArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import snowball049.roguelikemc.RoguelikeMC;
import snowball049.roguelikemc.RoguelikeMCStateSaverAndLoader;
import snowball049.roguelikemc.command.RoguelikeMCCommands;
import snowball049.roguelikemc.compat.RoguelikeMCCompat;
import snowball049.roguelikemc.config.RoguelikeMCCommonConfig;
import snowball049.roguelikemc.data.RoguelikeMCPlayerData;
import snowball049.roguelikemc.item.RoguelikeMCItemGroup;
import snowball049.roguelikemc.item.RoguelikeMCItems;
import snowball049.roguelikemc.item.UpgradePointOrbItem;
import snowball049.roguelikemc.network.packet.*;

public class RoguelikeMCRegisterUtil {

    public static void networkPacketRegister() {
        // Register network packets here
        PayloadTypeRegistry.playC2S().register(RefreshUpgradeOptionC2SPayload.ID, RefreshUpgradeOptionC2SPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(UpgradeOptionS2CPayload.ID, UpgradeOptionS2CPayload.CODEC);
        PayloadTypeRegistry.playC2S().register(SelectUpgradeOptionC2SPayload.ID, SelectUpgradeOptionC2SPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(RefreshCurrentUpgradeS2CPayload.ID, RefreshCurrentUpgradeS2CPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(SendUpgradePointsS2CPayload.ID, SendUpgradePointsS2CPayload.CODEC);
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
    }

    public static void onJoinEventRegister(ServerPlayNetworkHandler serverPlayNetworkHandler, PacketSender packetSender, MinecraftServer minecraftServer){
        ServerPlayerEntity player = serverPlayNetworkHandler.getPlayer();
        RoguelikeMCPlayerData playerData = RoguelikeMCStateSaverAndLoader.getPlayerState(player);
        ServerPlayNetworking.send(player, new RefreshCurrentUpgradeS2CPayload(true, playerData.permanentUpgrades));
        ServerPlayNetworking.send(player, new RefreshCurrentUpgradeS2CPayload(false, playerData.temporaryUpgrades));
        playerData.permanentUpgrades.forEach(upgrade -> RoguelikeMCUpgradeUtil.applyUpgrade(player, upgrade));
        playerData.temporaryUpgrades.forEach(upgrade -> RoguelikeMCUpgradeUtil.applyUpgrade(player, upgrade));
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

                )
        );
    }
    public static void onKillEntityEventRegister(ServerWorld server, Entity entity, LivingEntity context) {
        if(RoguelikeMCCommonConfig.INSTANCE.enableUpgradeSystem && RoguelikeMCCommonConfig.INSTANCE.enableKillHostileEntityUpgrade) {
            if (entity instanceof ServerPlayerEntity && context instanceof HostileEntity && !context.isPlayer()) {
                RoguelikeMCPlayerData playerData = RoguelikeMCStateSaverAndLoader.getPlayerState((ServerPlayerEntity) entity);
                playerData.currentKillHostile++;
                while (playerData.currentKillHostile >= playerData.currentKillHostileRequirement) {
                    playerData.currentKillHostile -= playerData.currentKillHostileRequirement;
                    playerData.currentKillHostileRequirement = Math.min(
                            playerData.currentKillHostileRequirement + RoguelikeMCCommonConfig.INSTANCE.amountBetweenKillHostileEntityUpgrade,
                            RoguelikeMCCommonConfig.INSTANCE.killHostileEntityRequirementMinMax.getLast()
                    );
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
        }
        // Handle effect to mob entity
        if (minecraftServer.getTicks() % 20 == 0) {
            RoguelikeMCUpgradeUtil.tickEffectToMobEntity(minecraftServer);
        }
    }
}
