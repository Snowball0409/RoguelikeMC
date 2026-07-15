package snowball049.roguelikemc.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.IdentifierArgumentType;
import net.minecraft.server.command.CommandManager;
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
import snowball049.roguelikemc.upgrade.apply.UpgradeApplier;
import snowball049.roguelikemc.upgrade.enums.UpgradePersistence;
import snowball049.roguelikemc.upgrade.point.UpgradePointService;
import snowball049.roguelikemc.upgrade.runtime.UpgradeTriggerRuntimeService;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public final class RoguelikeMCCommands {
    private RoguelikeMCCommands() {
    }

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("roguelikemc")
                .then(CommandManager.argument(RoguelikeMCCommandConstants.ARG_PLAYER, EntityArgumentType.players())
                        .then(CommandManager.literal("upgrade")
                                .then(CommandManager.literal("grant")
                                        .then(CommandManager.argument(RoguelikeMCCommandConstants.ARG_UPGRADE_OPTION, IdentifierArgumentType.identifier()).suggests(new UpgradeSuggestionProvider())
                                                .executes(RoguelikeMCCommands::grantUpgrade)
                                        )
                                )
                                .then(CommandManager.literal(RoguelikeMCCommandConstants.LITERAL_REMOVE)
                                        .then(CommandManager.argument(RoguelikeMCCommandConstants.ARG_UPGRADE_OPTION, IdentifierArgumentType.identifier()).suggests(new UpgradeSuggestionProvider())
                                                .executes(RoguelikeMCCommands::removeUpgrade)
                                        )
                                )
                                .then(CommandManager.literal("clear")
                                        .executes(RoguelikeMCCommands::clearUpgrade)
                                )
                        )
                        .then(CommandManager.literal("point")
                                .then(CommandManager.literal("add")
                                        .then(CommandManager.argument(RoguelikeMCCommandConstants.ARG_AMOUNT, IntegerArgumentType.integer(1))
                                                .executes(RoguelikeMCCommands::addPoint)
                                        )
                                )
                                .then(CommandManager.literal(RoguelikeMCCommandConstants.LITERAL_REMOVE)
                                        .then(CommandManager.argument(RoguelikeMCCommandConstants.ARG_AMOUNT, IntegerArgumentType.integer(1))
                                                .executes(RoguelikeMCCommands::removePoint)
                                        )
                                )
                                .then(CommandManager.literal("set")
                                        .then(CommandManager.argument(RoguelikeMCCommandConstants.ARG_AMOUNT, IntegerArgumentType.integer(1))
                                                .executes(RoguelikeMCCommands::setPoint)
                                        )
                                )
                                .then(CommandManager.literal("get")
                                        .executes(RoguelikeMCCommands::getPoint))
                        )
                        .then(CommandManager.literal("upgrade_pool")
                                .then(CommandManager.literal("add")
                                        .then(CommandManager.argument(RoguelikeMCCommandConstants.ARG_UPGRADE_POOL_OPTION, IdentifierArgumentType.identifier()).suggests(new UpgradePoolSuggestionProvider())
                                                .executes(RoguelikeMCCommands::addUpgradePool))
                                )
                                .then(CommandManager.literal(RoguelikeMCCommandConstants.LITERAL_REMOVE)
                                        .then(CommandManager.argument(RoguelikeMCCommandConstants.ARG_UPGRADE_POOL_OPTION, IdentifierArgumentType.identifier()).suggests(new UpgradePoolSuggestionProvider())
                                                .executes(RoguelikeMCCommands::removeUpgradePool))
                                )
                                .then(CommandManager.literal("get")
                                        .executes(RoguelikeMCCommands::getUpgradePool))
                        )
                )
        );
    }

    public static int grantUpgrade(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        try {
            List<ServerPlayerEntity> players = EntityArgumentType.getPlayers(context, RoguelikeMCCommandConstants.ARG_PLAYER).stream().toList();
            Identifier upgradeId = IdentifierArgumentType.getIdentifier(context, RoguelikeMCCommandConstants.ARG_UPGRADE_OPTION);
            RoguelikeMCUpgradeData upgrade = RoguelikeMCUpgradeManager.getUpgrade(upgradeId);

            if (upgrade == null) {
                context.getSource().sendError(Text.literal("Upgrade '" + upgradeId + "' not found!"));
                return 0;
            }

            players.forEach(player -> UpgradeApplier.addUpgrade(upgrade, player));
            return Command.SINGLE_SUCCESS;
        } catch (CommandSyntaxException e) {
            context.getSource().sendError(Text.literal(RoguelikeMCCommandConstants.ERROR_PARSING_PLAYERS + e.getMessage()));
            throw e;
        }
    }

    public static int removeUpgrade(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        try {
            List<ServerPlayerEntity> players = EntityArgumentType.getPlayers(context, RoguelikeMCCommandConstants.ARG_PLAYER).stream().toList();
            Identifier upgradeId = IdentifierArgumentType.getIdentifier(context, RoguelikeMCCommandConstants.ARG_UPGRADE_OPTION);
            RoguelikeMCUpgradeData upgrade = RoguelikeMCUpgradeManager.getUpgrade(upgradeId);

            if (upgrade == null) {
                context.getSource().sendError(Text.literal("Upgrade '" + upgradeId + "' not found!"));
                return 0;
            } else {
                players.forEach(player -> {
                    RoguelikeMCPlayerData playerData = RoguelikeMCStateSaverAndLoader.getPlayerState(player);
                    boolean permanent = upgrade.persistence() == UpgradePersistence.PERMANENT;
                    boolean removed = permanent
                            ? playerData.permanentUpgradeIds.removeIf(id -> id.equals(upgradeId))
                            : playerData.temporaryUpgradeIds.removeIf(id -> id.equals(upgradeId));
                    if (removed) {
                        UpgradeApplier.removeUpgrade(player, upgrade);
                        UpgradeTriggerRuntimeService.clearUpgrade(player, upgradeId);
                        ServerPlayNetworking.send(
                                player,
                                new RefreshCurrentUpgradeS2CPayload(
                                        permanent,
                                        permanent ? playerData.getPermanentUpgrades() : playerData.getTemporaryUpgrades()
                                )
                        );
                        player.sendMessage(Text.of(RoguelikeMCCommandConstants.MESSAGE_UPGRADE_REMOVED + upgrade.name()));
                    } else {
                        player.sendMessage(Text.of(RoguelikeMCCommandConstants.MESSAGE_UPGRADE_NOT_OWNED + upgrade.name()));
                    }
                });
            }
        } catch (CommandSyntaxException e) {
            context.getSource().sendError(Text.literal(RoguelikeMCCommandConstants.ERROR_PARSING_PLAYERS + e.getMessage()));
            throw e;
        }
        return Command.SINGLE_SUCCESS;
    }

    public static int clearUpgrade(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        try {
            List<ServerPlayerEntity> players = EntityArgumentType.getPlayers(context, RoguelikeMCCommandConstants.ARG_PLAYER).stream().toList();
            players.forEach(player -> {
                RoguelikeMCPlayerData playerData = RoguelikeMCStateSaverAndLoader.getPlayerState(player);
                playerData.getPermanentUpgrades().forEach(upgrade -> UpgradeApplier.removeUpgrade(player, upgrade));
                playerData.getTemporaryUpgrades().forEach(upgrade -> UpgradeApplier.removeUpgrade(player, upgrade));
                playerData.permanentUpgradeIds.clear();
                playerData.temporaryUpgradeIds.clear();
                UpgradeTriggerRuntimeService.clearPlayer(player);
                UpgradeApplier.syncOwnedUpgrades(player, playerData);
                player.sendMessage(Text.of("You have been cleared all upgrades!"));
            });
        } catch (CommandSyntaxException e) {
            context.getSource().sendError(Text.literal(RoguelikeMCCommandConstants.ERROR_PARSING_PLAYERS + e.getMessage()));
            throw e;
        }
        return Command.SINGLE_SUCCESS;
    }

    public static int addPoint(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        List<ServerPlayerEntity> players = EntityArgumentType.getPlayers(context, RoguelikeMCCommandConstants.ARG_PLAYER).stream().toList();
        int amount = IntegerArgumentType.getInteger(context, RoguelikeMCCommandConstants.ARG_AMOUNT);

        players.forEach(player -> UpgradePointService.addUpgradePoints(player, amount));

        return Command.SINGLE_SUCCESS;
    }

    public static int removePoint(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        List<ServerPlayerEntity> players = EntityArgumentType.getPlayers(context, RoguelikeMCCommandConstants.ARG_PLAYER).stream().toList();
        int amount = IntegerArgumentType.getInteger(context, RoguelikeMCCommandConstants.ARG_AMOUNT);

        players.forEach(player -> {
            boolean isRemoved = UpgradePointService.removeUpgradePoints(player, amount);
            if (isRemoved)
                player.sendMessage(Text.of("You have been removed " + amount + " upgrade points!"), false);
        });
        return Command.SINGLE_SUCCESS;
    }

    public static int setPoint(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        List<ServerPlayerEntity> players = EntityArgumentType.getPlayers(context, RoguelikeMCCommandConstants.ARG_PLAYER).stream().toList();
        int amount = IntegerArgumentType.getInteger(context, RoguelikeMCCommandConstants.ARG_AMOUNT);

        players.forEach(player -> UpgradePointService.setUpgradePoints(player, amount));
        return Command.SINGLE_SUCCESS;
    }

    public static int getPoint(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        List<ServerPlayerEntity> players = EntityArgumentType.getPlayers(context, RoguelikeMCCommandConstants.ARG_PLAYER).stream().toList();

        players.forEach(player -> UpgradePointService.getUpgradePoints(context.getSource(), player));
        return Command.SINGLE_SUCCESS;
    }

    public static int addUpgradePool(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        List<ServerPlayerEntity> players = EntityArgumentType.getPlayers(context, RoguelikeMCCommandConstants.ARG_PLAYER).stream().toList();
        players.forEach(player -> {
            Identifier upgradePoolId = IdentifierArgumentType.getIdentifier(context, RoguelikeMCCommandConstants.ARG_UPGRADE_POOL_OPTION);
            RoguelikeMCPlayerData playerData = RoguelikeMCStateSaverAndLoader.getPlayerState(player);
            playerData.activeUpgradePools.add(upgradePoolId);
            player.sendMessage(Text.literal("You have been added upgrade pool: " + upgradePoolId), false);
        });
        return Command.SINGLE_SUCCESS;
    }

    public static int removeUpgradePool(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        List<ServerPlayerEntity> players = EntityArgumentType.getPlayers(context, RoguelikeMCCommandConstants.ARG_PLAYER).stream().toList();
        players.forEach(player -> {
            Identifier upgradePoolId = IdentifierArgumentType.getIdentifier(context, RoguelikeMCCommandConstants.ARG_UPGRADE_POOL_OPTION);
            RoguelikeMCPlayerData playerData = RoguelikeMCStateSaverAndLoader.getPlayerState(player);
            playerData.activeUpgradePools.remove(upgradePoolId);
            player.sendMessage(Text.literal("You have been removed upgrade pool: " + upgradePoolId), false);
        });
        return Command.SINGLE_SUCCESS;
    }

    public static int getUpgradePool(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        List<ServerPlayerEntity> players = EntityArgumentType.getPlayers(context, RoguelikeMCCommandConstants.ARG_PLAYER).stream().toList();
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
        public CompletableFuture<Suggestions> getSuggestions(CommandContext<ServerCommandSource> commandContext, SuggestionsBuilder suggestionsBuilder) {
            String input = suggestionsBuilder.getRemaining().toLowerCase();
            RoguelikeMCUpgradeManager.getUpgradeIds().forEach(identifier -> {
                if (identifier.toString().toLowerCase().contains(input))
                    suggestionsBuilder.suggest(identifier.toString());
            });
            return suggestionsBuilder.buildFuture();
        }
    }

    public static class UpgradePoolSuggestionProvider implements SuggestionProvider<ServerCommandSource> {
        @Override
        public CompletableFuture<Suggestions> getSuggestions(CommandContext<ServerCommandSource> context, SuggestionsBuilder builder) {
            String input = builder.getRemaining().toLowerCase();
            RoguelikeMCUpgradePoolManager.getUpgradePools().forEach(identifier -> {
                if (identifier.toString().toLowerCase().contains(input))
                    builder.suggest(identifier.toString());
            });
            return builder.buildFuture();
        }
    }
}
