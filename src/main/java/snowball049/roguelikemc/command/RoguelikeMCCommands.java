package snowball049.roguelikemc.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.IdentifierArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import snowball049.roguelikemc.RoguelikeMCStateSaverAndLoader;
import snowball049.roguelikemc.data.RoguelikeMCPlayerData;
import snowball049.roguelikemc.data.RoguelikeMCUpgradeData;
import snowball049.roguelikemc.network.packet.RefreshCurrentUpgradeS2CPayload;
import snowball049.roguelikemc.upgrade.RoguelikeMCUpgradeManager;
import snowball049.roguelikemc.upgrade.RoguelikeMCUpgradePoolManager;
import snowball049.roguelikemc.util.RoguelikeMCPointUtil;
import snowball049.roguelikemc.util.RoguelikeMCUpgradeUtil;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class RoguelikeMCCommands {

    public static int grantUpgrade(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        try {
            List<ServerPlayerEntity> players = EntityArgumentType.getPlayers(context, "player").stream().toList();
            Identifier upgradeId = IdentifierArgumentType.getIdentifier(context, "upgradeOption");
            RoguelikeMCUpgradeData upgrade = RoguelikeMCUpgradeManager.getUpgrade(upgradeId);

            if (upgrade == null) {
                context.getSource().sendError(Text.literal("Upgrade '" + upgradeId + "' not found!"));
                return 0;
            }

            players.forEach(player -> RoguelikeMCUpgradeUtil.addUpgrade(upgrade, player));
            return Command.SINGLE_SUCCESS;
        } catch (CommandSyntaxException e) {
            context.getSource().sendError(Text.literal("Error parsing players: " + e.getMessage()));
            throw e;
        }
    }

    public static int removeUpgrade(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        try{
            List<ServerPlayerEntity> players = EntityArgumentType.getPlayers(context, "player").stream().toList();
            Identifier upgradeId = IdentifierArgumentType.getIdentifier(context, "upgradeOption");
            RoguelikeMCUpgradeData upgrade = RoguelikeMCUpgradeManager.getUpgrade(upgradeId);

            if (upgrade == null) {
                context.getSource().sendError(Text.literal("Upgrade '" + upgradeId + "' not found!"));
                return 0;
            }else {
                players.forEach(player -> {
                    RoguelikeMCPlayerData playerData = RoguelikeMCStateSaverAndLoader.getPlayerState(player);
                    boolean removed;
                    if(upgrade.isPermanent()) {
                        removed = playerData.permanentUpgrades.removeIf(u -> u.equals(RoguelikeMCUpgradeManager.getUpgrade(upgradeId)));
                        ServerPlayNetworking.send(player, new RefreshCurrentUpgradeS2CPayload(true, playerData.permanentUpgrades));
                    }else {
                        removed = playerData.temporaryUpgrades.removeIf(u -> u.equals(RoguelikeMCUpgradeManager.getUpgrade(upgradeId)));
                        ServerPlayNetworking.send(player, new RefreshCurrentUpgradeS2CPayload(false, playerData.temporaryUpgrades));
                    }
                    if(removed){
                        upgrade.actions().forEach(upgradeAction -> {
                            RoguelikeMCUpgradeUtil.removeUpgrade(player, upgradeId, upgradeAction);
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
                        RoguelikeMCUpgradeUtil.removeUpgrade(player, RoguelikeMCUpgradeManager.getUpgradeId(upgrade), upgradeAction);
                    });
                });
                playerData.temporaryUpgrades.forEach(upgrade -> {
                    upgrade.actions().forEach(upgradeAction -> {
                        RoguelikeMCUpgradeUtil.removeUpgrade(player, RoguelikeMCUpgradeManager.getUpgradeId(upgrade), upgradeAction);
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
            RoguelikeMCPointUtil.addUpgradePoints(player, amount);
        });

        return Command.SINGLE_SUCCESS;
    }

    public static int removePoint(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        List<ServerPlayerEntity> players = EntityArgumentType.getPlayers(context, "player").stream().toList();
        int amount = IntegerArgumentType.getInteger(context, "amount");

        players.forEach(player -> {
            boolean isRemoved = RoguelikeMCPointUtil.removeUpgradePoints(player, amount);
            if(isRemoved)
                player.sendMessage(Text.of("You have been removed " + amount + " upgrade points!"), false);
        });
        return Command.SINGLE_SUCCESS;
    }

    public static int setPoint(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        List<ServerPlayerEntity> players = EntityArgumentType.getPlayers(context, "player").stream().toList();
        int amount = IntegerArgumentType.getInteger(context, "amount");

        players.forEach(player -> {
            RoguelikeMCPointUtil.setUpgradePoints(player, amount);
        });
        return Command.SINGLE_SUCCESS;
    }

    public static int getPoint(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        List<ServerPlayerEntity> players = EntityArgumentType.getPlayers(context, "player").stream().toList();

        players.forEach(player -> {
            RoguelikeMCPointUtil.getUpgradePoints(context.getSource(), player);
        });
        return Command.SINGLE_SUCCESS;
    }

    public static int addUpgradePool(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        List<ServerPlayerEntity> players = EntityArgumentType.getPlayers(context, "player").stream().toList();
        players.forEach(player -> {
            Identifier upgradePoolId = IdentifierArgumentType.getIdentifier(context, "upgradePoolOption");
            RoguelikeMCPlayerData playerData = RoguelikeMCStateSaverAndLoader.getPlayerState(player);
            playerData.activeUpgradePools.add(upgradePoolId);
            player.sendMessage(Text.literal("You have been added upgrade pool: " + upgradePoolId), false);
        });
        return Command.SINGLE_SUCCESS;
    }

    public static int removeUpgradePool(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        List<ServerPlayerEntity> players = EntityArgumentType.getPlayers(context, "player").stream().toList();
        players.forEach(player -> {
            Identifier upgradePoolId = IdentifierArgumentType.getIdentifier(context, "upgradePoolOption");
            RoguelikeMCPlayerData playerData = RoguelikeMCStateSaverAndLoader.getPlayerState(player);
            playerData.activeUpgradePools.remove(upgradePoolId);
            player.sendMessage(Text.literal("You have been removed upgrade pool: " + upgradePoolId), false);
        });
        return Command.SINGLE_SUCCESS;
    }

    public static int getUpgradePool(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        List<ServerPlayerEntity> players = EntityArgumentType.getPlayers(context, "player").stream().toList();
        players.forEach(player -> {
            RoguelikeMCPlayerData playerData = RoguelikeMCStateSaverAndLoader.getPlayerState(player);
            context.getSource().sendMessage(Text.of(player.getName().getString() + " have " + playerData.activeUpgradePools.size() + " upgrade pools!"));
            Text upgradePoolText = Text.literal("Upgrade Pools: ").formatted(Formatting.GRAY);
            for (Iterator<Identifier> it = playerData.activeUpgradePools.iterator(); it.hasNext(); ) {
                Identifier upgradePool = it.next();
                upgradePoolText = upgradePoolText.copy()
                        .append(Text.literal(upgradePool.toString()).formatted(Formatting.GRAY, Formatting.ITALIC));
                if (it.hasNext()) {
                    upgradePoolText = upgradePoolText.copy().append(Text.literal(", ").formatted(Formatting.GRAY));
                }
            }
            context.getSource().sendMessage(upgradePoolText);
        });
        return Command.SINGLE_SUCCESS;
    }

    public static class UpgradeSuggestionProvider implements SuggestionProvider<ServerCommandSource> {
        @Override
        public CompletableFuture<Suggestions> getSuggestions(CommandContext commandContext, SuggestionsBuilder suggestionsBuilder){
            String input = suggestionsBuilder.getRemaining().toLowerCase();
            RoguelikeMCUpgradeManager.getUpgradeIds().forEach((identifier -> {
                if(identifier.toString().toLowerCase().contains(input))
                    suggestionsBuilder.suggest(identifier.toString());
            }));
            return suggestionsBuilder.buildFuture();
        }
    }

    public static class UpgradePoolSuggestionProvider implements SuggestionProvider<ServerCommandSource> {
        @Override
        public CompletableFuture<Suggestions> getSuggestions(CommandContext<ServerCommandSource> context, SuggestionsBuilder builder) {
            String input = builder.getRemaining().toLowerCase();
            RoguelikeMCUpgradePoolManager.getUpgradePools().forEach((identifier -> {
                if(identifier.toString().toLowerCase().contains(input))
                    builder.suggest(identifier.toString());
            }));
            return builder.buildFuture();
        }
    }
}