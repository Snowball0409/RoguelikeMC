package snowball049.roguelikemc.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.datafixers.util.Pair;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.registry.Registries;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import snowball049.roguelikemc.RoguelikeMCStateSaverAndLoader;
import snowball049.roguelikemc.data.RoguelikeMCPlayerData;
import snowball049.roguelikemc.data.RoguelikeMCUpgradeData;
import snowball049.roguelikemc.network.packet.RefreshCurrentUpgradeS2CPayload;
import snowball049.roguelikemc.upgrade.RoguelikeMCUpgradeManager;
import snowball049.roguelikemc.util.RoguelikeMCUpgradeUtil;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class RoguelikeMCCommands {

    public static int grantUpgrade(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        try {
            List<ServerPlayerEntity> players = EntityArgumentType.getPlayers(context, "player").stream().toList();
            String upgradeId = StringArgumentType.getString(context, "upgradeOption");
//            RoguelikeMCUpgradesConfig.RogueLikeMCUpgradeConfig upgrade = RoguelikeMCUpgradesConfig.INSTANCE.upgrades.get(upgradeId);
            RoguelikeMCUpgradeData upgrade = RoguelikeMCUpgradeManager.getUpgrades().stream().filter(u -> u.id().equals(upgradeId)).findFirst().orElse(null);

            if (upgrade == null) {
                context.getSource().sendError(Text.literal("Upgrade '" + upgradeId + "' not found!"));
                return 0;
            }

            players.forEach(player -> RoguelikeMCUpgradeUtil.handleUpgrade(upgrade, player));
            return Command.SINGLE_SUCCESS;
        } catch (CommandSyntaxException e) {
            context.getSource().sendError(Text.literal("Error parsing players: " + e.getMessage()));
            throw e;
        }
    }

    public static int removeUpgrade(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        try{
            List<ServerPlayerEntity> players = EntityArgumentType.getPlayers(context, "player").stream().toList();
            String upgradeId = StringArgumentType.getString(context, "upgradeOption");
            RoguelikeMCUpgradeData upgrade = RoguelikeMCUpgradeManager.getUpgrades().stream().filter(u -> u.id().equals(upgradeId)).findFirst().orElse(null);

            if (upgrade == null) {
                context.getSource().sendError(Text.literal("Upgrade '" + upgradeId + "' not found!"));
                return 0;
            }else {
                players.forEach(player -> {
                    RoguelikeMCPlayerData playerData = RoguelikeMCStateSaverAndLoader.getPlayerState(player);
                    boolean removed;
                    if(upgrade.isPermanent()) {
                        removed = playerData.permanentUpgrades.removeIf(u -> u.id().equals(upgradeId));
                        ServerPlayNetworking.send(player, new RefreshCurrentUpgradeS2CPayload(true, playerData.permanentUpgrades));
                    }else {
                        removed = playerData.temporaryUpgrades.removeIf(u -> u.id().equals(upgradeId));
                        ServerPlayNetworking.send(player, new RefreshCurrentUpgradeS2CPayload(false, playerData.temporaryUpgrades));
                    }
                    if(removed){
                        upgrade.actions().forEach(upgradeAction -> {
                            if(upgradeAction.type().equals("attribute")){
                                RoguelikeMCUpgradeUtil.removeUpgradeAttribute(player, upgradeAction.value());
                            } else if (upgradeAction.type().equals("effect")) {
                                RoguelikeMCUpgradeUtil.removeUpgradeEffect(player, upgradeAction.value());
                            }
                        });
                    }
                    player.sendMessage(Text.of("You have been removed upgrade: "+ upgrade.name()));
                });
            }

        }catch (CommandSyntaxException e){
            context.getSource().sendError(Text.literal("Error parsing players: " + e.getMessage()));
            throw e;
        }
        return Command.SINGLE_SUCCESS;
    }

    public static int clearUpgrade(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        try {
            List<ServerPlayerEntity> players = EntityArgumentType.getPlayers(context, "player").stream().toList();
            players.forEach(player -> {
                RoguelikeMCPlayerData playerData = RoguelikeMCStateSaverAndLoader.getPlayerState(player);
                playerData.permanentUpgrades.forEach(upgrade -> {
                    upgrade.actions().forEach(upgradeAction -> {
                        if(upgradeAction.type().equals("attribute")){
                            RoguelikeMCUpgradeUtil.removeUpgradeAttribute(player, upgradeAction.value());
                        } else if (upgradeAction.type().equals("effect")) {
                            player.removeStatusEffect(Registries.STATUS_EFFECT.getEntry(Identifier.tryParse(upgradeAction.value().getFirst())).orElseThrow());
                        }
                    });
                });
                playerData.temporaryUpgrades.forEach(upgrade -> {
                    upgrade.actions().forEach(upgradeAction -> {
                        if(upgradeAction.type().equals("attribute")){
                            RoguelikeMCUpgradeUtil.removeUpgradeAttribute(player, upgradeAction.value());
                        } else if (upgradeAction.type().equals("effect")) {
                            player.removeStatusEffect(Registries.STATUS_EFFECT.getEntry(Identifier.tryParse(upgradeAction.value().getFirst())).orElseThrow());
                        }
                    });
                });
                playerData.permanentUpgrades.clear();
                playerData.temporaryUpgrades.clear();
                ServerPlayNetworking.send(player, new RefreshCurrentUpgradeS2CPayload(true, playerData.permanentUpgrades));
                ServerPlayNetworking.send(player, new RefreshCurrentUpgradeS2CPayload(false, playerData.temporaryUpgrades));
                player.sendMessage(Text.of("You have been cleared all upgrades!"));
            });
        }catch (CommandSyntaxException e){
            context.getSource().sendError(Text.literal("Error parsing players: " + e.getMessage()));
            throw e;
        }
        return Command.SINGLE_SUCCESS;
    }

    public static int addPoint(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        List<ServerPlayerEntity> players = EntityArgumentType.getPlayers(context, "player").stream().toList();
        int amount = IntegerArgumentType.getInteger(context, "amount");

        players.forEach(player -> {
            RoguelikeMCPlayerData playerData = RoguelikeMCStateSaverAndLoader.getPlayerState(player);
            playerData.upgradePoints += amount;
            player.sendMessage(Text.of("You have been granted "+ amount +" upgrade point!"));
        });

        return Command.SINGLE_SUCCESS;
    }

    public static int removePoint(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        List<ServerPlayerEntity> players = EntityArgumentType.getPlayers(context, "player").stream().toList();
        int amount = IntegerArgumentType.getInteger(context, "amount");

        players.forEach(player -> {
            RoguelikeMCPlayerData playerData = RoguelikeMCStateSaverAndLoader.getPlayerState(player);
            playerData.upgradePoints = Integer.max(playerData.upgradePoints - amount, 0);
            player.sendMessage(Text.of("You have been removed "+ amount +" upgrade point!"));
        });
        return Command.SINGLE_SUCCESS;
    }

    public static class UpgradeSuggestionProvider implements SuggestionProvider<ServerCommandSource> {
        @Override
        public CompletableFuture<Suggestions> getSuggestions(CommandContext commandContext, SuggestionsBuilder suggestionsBuilder){
            List<RoguelikeMCUpgradeData> upgrades = RoguelikeMCUpgradeManager.getUpgrades().stream().toList();
            upgrades.forEach((upgrade) -> suggestionsBuilder.suggest(upgrade.id()));
            return suggestionsBuilder.buildFuture();
        }
    }
}