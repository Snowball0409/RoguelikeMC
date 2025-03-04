package snowball049.roguelikemc.command;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class RoguelikeMCCommands {
    private static final String[] LEVEL = {"common", "rare", "epic", "legendary", "fate"};

    public static int executeCommandUpgrade(CommandContext<ServerCommandSource> context) {
        String tier = context.getArgument("tier", String.class);
        ArrayList<String> commonUpgrades = new ArrayList<>();

        commonUpgrades.add("+1 attack");
        commonUpgrades.add("+1 health");
        commonUpgrades.add("+10% speed");
        commonUpgrades.add("+10% attack speed");

        switch (tier) {
            case "common":
                Collections.shuffle(commonUpgrades);
                List<String> selectedUpgrades = commonUpgrades.subList(0, 3);
                ServerPlayerEntity player = context.getSource().getPlayer();
                if (player != null) {
                    player.sendMessage(Text.literal("§a選擇一個升級："), false);
                    for (String upgrade : selectedUpgrades) {
                        Text upgradeText = Text.literal("§6» " + upgrade)
                                .styled(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/apply_upgrade \"" + upgrade + "\"")));
                        player.sendMessage(upgradeText, false);
                    }
                }

                break;
            case "rare":
                break;
            default:
                context.getSource().sendFeedback(()->Text.literal("Invalid Tier:" + tier), false);
        }
        return 1;
    }

    public static CompletableFuture<Suggestions> suggestUpgrades(CommandContext<ServerCommandSource> serverCommandSourceCommandContext, SuggestionsBuilder suggestionsBuilder) {
        for(String level: LEVEL) {
            suggestionsBuilder.suggest(level);
        }
        return suggestionsBuilder.buildFuture();
    }
}
