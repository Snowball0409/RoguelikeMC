package snowball049.roguelikemc.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import snowball049.roguelikemc.RoguelikeMCStateSaverAndLoader;
import snowball049.roguelikemc.config.RoguelikeMCUpgradesConfig;
import snowball049.roguelikemc.data.RoguelikeMCPlayerData;
import snowball049.roguelikemc.network.packet.RefreshCurrentUpgradeS2CPayload;
import snowball049.roguelikemc.util.RoguelikeMCUpgradeUtil;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class RoguelikeMCCommands {
    public static int grantUpgrade(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        try {
            List<ServerPlayer> players = EntityArgument.getPlayers(context, "player").stream().toList();
            String upgradeId = StringArgumentType.getString(context, "upgradeOption");
            RoguelikeMCUpgradesConfig.RogueLikeMCUpgradeConfig upgrade = RoguelikeMCUpgradesConfig.INSTANCE.upgrades.get(upgradeId);

            if (upgrade == null) {
                context.getSource().sendFailure(Component.literal("Upgrade '" + upgradeId + "' not found!"));
                return 0;
            }

            players.forEach(player -> RoguelikeMCUpgradeUtil.handleUpgrade(upgrade, player));
            return Command.SINGLE_SUCCESS;
        } catch (CommandSyntaxException e) {
            context.getSource().sendFailure(Component.literal("Error parsing players: " + e.getMessage()));
            throw e;
        }
    }

    public static int removeUpgrade(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        try{
            List<ServerPlayer> players = EntityArgument.getPlayers(context, "player").stream().toList();
            String upgradeId = StringArgumentType.getString(context, "upgradeOption");
            RoguelikeMCUpgradesConfig.RogueLikeMCUpgradeConfig upgrade = RoguelikeMCUpgradesConfig.INSTANCE.upgrades.get(upgradeId);

            if (upgrade == null) {
                context.getSource().sendFailure(Component.literal("Upgrade '" + upgradeId + "' not found!"));
                return 0;
            }else {
                players.forEach(player -> {
                    RoguelikeMCPlayerData playerData = RoguelikeMCStateSaverAndLoader.getPlayerState(player);
                    boolean removed = false;
                    if(upgrade.is_permanent()) {
                        removed = playerData.permanentUpgrades.removeIf(u -> u.id().equals(upgradeId));
                        ServerPlayNetworking.send(player, new RefreshCurrentUpgradeS2CPayload(playerData.permanentUpgrades));
                    }else {
                        removed = playerData.temporaryUpgrades.removeIf(u -> u.id().equals(upgradeId));
                        ServerPlayNetworking.send(player, new RefreshCurrentUpgradeS2CPayload(playerData.temporaryUpgrades));
                    }
                    if(removed){
                        upgrade.action().forEach(upgradeAction -> {
                            if(upgradeAction.type().equals("attribute")){
                                RoguelikeMCUpgradeUtil.removeUpgradeAttribute(player, upgradeAction.value());
                            } else if (upgradeAction.type().equals("effect")) {
                                player.removeEffect(BuiltInRegistries.MOB_EFFECT.getHolder(ResourceLocation.tryParse(upgradeAction.value().getFirst())).orElseThrow());
                            }
                        });
                    }
                    player.sendSystemMessage(Component.nullToEmpty("You have been removed upgrade: "+ upgrade.name()));
                });
            }

        }catch (CommandSyntaxException e){
            context.getSource().sendFailure(Component.literal("Error parsing players: " + e.getMessage()));
            throw e;
        }
        return Command.SINGLE_SUCCESS;
    }

    public static int clearUpgrade(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        try {
            List<ServerPlayer> players = EntityArgument.getPlayers(context, "player").stream().toList();
            players.forEach(player -> {
                RoguelikeMCPlayerData playerData = RoguelikeMCStateSaverAndLoader.getPlayerState(player);
                playerData.permanentUpgrades.forEach(upgrade -> {
                    upgrade.action().forEach(upgradeAction -> {
                        if(upgradeAction.type().equals("attribute")){
                            RoguelikeMCUpgradeUtil.removeUpgradeAttribute(player, upgradeAction.value());
                        } else if (upgradeAction.type().equals("effect")) {
                            player.removeEffect(BuiltInRegistries.MOB_EFFECT.getHolder(ResourceLocation.tryParse(upgradeAction.value().getFirst())).orElseThrow());
                        }
                    });
                });
                playerData.temporaryUpgrades.forEach(upgrade -> {
                    upgrade.action().forEach(upgradeAction -> {
                        if(upgradeAction.type().equals("attribute")){
                            RoguelikeMCUpgradeUtil.removeUpgradeAttribute(player, upgradeAction.value());
                        } else if (upgradeAction.type().equals("effect")) {
                            player.removeEffect(BuiltInRegistries.MOB_EFFECT.getHolder(ResourceLocation.tryParse(upgradeAction.value().getFirst())).orElseThrow());
                        }
                    });
                });
                playerData.permanentUpgrades.clear();
                playerData.temporaryUpgrades.clear();
                ServerPlayNetworking.send(player, new RefreshCurrentUpgradeS2CPayload(playerData.permanentUpgrades));
                ServerPlayNetworking.send(player, new RefreshCurrentUpgradeS2CPayload(playerData.temporaryUpgrades));
                player.sendSystemMessage(Component.nullToEmpty("You have been cleared all upgrades!"));
            });
        }catch (CommandSyntaxException e){
            context.getSource().sendFailure(Component.literal("Error parsing players: " + e.getMessage()));
            throw e;
        }
        return Command.SINGLE_SUCCESS;
    }

    public static int addPoint(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        List<ServerPlayer> players = EntityArgument.getPlayers(context, "player").stream().toList();
        int amount = IntegerArgumentType.getInteger(context, "amount");

        players.forEach(player -> {
            RoguelikeMCPlayerData playerData = RoguelikeMCStateSaverAndLoader.getPlayerState(player);
            playerData.upgradePoints += amount;
            player.sendSystemMessage(Component.nullToEmpty("You have been granted "+ amount +" upgrade point!"));
        });

        return Command.SINGLE_SUCCESS;
    }

    public static int removePoint(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        List<ServerPlayer> players = EntityArgument.getPlayers(context, "player").stream().toList();
        int amount = IntegerArgumentType.getInteger(context, "amount");

        players.forEach(player -> {
            RoguelikeMCPlayerData playerData = RoguelikeMCStateSaverAndLoader.getPlayerState(player);
            playerData.upgradePoints = Integer.max(playerData.upgradePoints - amount, 0);
            player.sendSystemMessage(Component.nullToEmpty("You have been removed "+ amount +" upgrade point!"));
        });
        return Command.SINGLE_SUCCESS;
    }

    public static class UpgradeSuggestionProvider implements SuggestionProvider<CommandSourceStack> {
        @Override
        public CompletableFuture<Suggestions> getSuggestions(CommandContext commandContext, SuggestionsBuilder suggestionsBuilder){
            Map<String, RoguelikeMCUpgradesConfig.RogueLikeMCUpgradeConfig> upgrades = RoguelikeMCUpgradesConfig.INSTANCE.upgrades;
            upgrades.forEach((id, upgrade) -> suggestionsBuilder.suggest(id));
            return suggestionsBuilder.buildFuture();
        }
    }
}