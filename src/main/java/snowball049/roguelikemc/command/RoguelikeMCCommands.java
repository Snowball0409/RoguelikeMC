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
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import snowball049.roguelikemc.RoguelikeMC;
import snowball049.roguelikemc.RoguelikeMCStateSaverAndLoader;
import snowball049.roguelikemc.config.RoguelikeMCUpgradesConfig;
import snowball049.roguelikemc.data.RoguelikeMCPlayerData;
import snowball049.roguelikemc.network.packet.RefreshCurrentUpgradeS2CPayload;
import snowball049.roguelikemc.util.RoguelikeMCUpgradeUtil;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class RoguelikeMCCommands {
    public static int grantUpgrade(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        try {
            List<ServerPlayerEntity> players = EntityArgumentType.getPlayers(context, "player").stream().toList();
            String upgradeId = StringArgumentType.getString(context, "upgradeOption");
            RoguelikeMCUpgradesConfig.RogueLikeMCUpgradeConfig upgrade = RoguelikeMCUpgradesConfig.INSTANCE.upgrades.get(upgradeId);

            if (upgrade == null) {
                context.getSource().sendError(Text.literal("Upgrade '" + upgradeId + "' not found!"));
                return 0;
            }

            players.forEach(player -> {
                RoguelikeMCUpgradeUtil.handleUpgrade(upgrade, player);
            });
            return Command.SINGLE_SUCCESS;
        } catch (CommandSyntaxException e) {
            context.getSource().sendError(Text.literal("Error parsing players: " + e.getMessage()));
            throw e;
        }
    }

    public static int removeUpgrade(CommandContext<ServerCommandSource> serverCommandSourceCommandContext) {
        return Command.SINGLE_SUCCESS;
    }

    public static int clearUpgrade(CommandContext<ServerCommandSource> serverCommandSourceCommandContext) {
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
        public CompletableFuture<Suggestions> getSuggestions(CommandContext commandContext, SuggestionsBuilder suggestionsBuilder) throws CommandSyntaxException {
            Map<String, RoguelikeMCUpgradesConfig.RogueLikeMCUpgradeConfig> upgrades = RoguelikeMCUpgradesConfig.INSTANCE.upgrades;
            upgrades.forEach((id, upgrade) -> {
                suggestionsBuilder.suggest(id);
            });
            return suggestionsBuilder.buildFuture();
        }
    }
}