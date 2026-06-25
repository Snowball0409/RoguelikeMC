package snowball049.roguelikemc.upgrade.action;

import net.minecraft.entity.passive.CatVariant;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import snowball049.roguelikemc.RoguelikeMC;
import snowball049.roguelikemc.upgrade.enums.UpgradeActionType;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public final class CommandUpgradeActionHandler implements UpgradeActionHandler {
    public static final CommandUpgradeActionHandler INSTANCE = new CommandUpgradeActionHandler();

    private CommandUpgradeActionHandler() {
    }

    @Override
    public UpgradeActionType type() {
        return UpgradeActionType.COMMAND;
    }

    @Override
    public void apply(UpgradeActionContext context) {
        executeCommand(context.player(), context.action().value());
    }

    private static void executeCommand(ServerPlayerEntity player, List<String> value) {
        String command = value.getFirst();
        MinecraftServer server = player.getServer();

        if (server == null) {
            RoguelikeMC.LOGGER.warn("Server is null");
            return;
        }

        if (Boolean.parseBoolean(value.getLast()) && command.startsWith("summon")) {
            int[] uuidInts = uuidToIntArray(player.getUuid());
            int nbtStart = command.indexOf('{');
            if (nbtStart != -1) {
                String beforeNBT = command.substring(0, nbtStart + 1);
                String afterNBT = command.substring(nbtStart + 1);
                String ownerTag = String.format("Owner:[I;%d,%d,%d,%d],", uuidInts[0], uuidInts[1], uuidInts[2], uuidInts[3]);
                command = beforeNBT + ownerTag + afterNBT;
            } else {
                String ownerTag = String.format("{Owner:[I;%d,%d,%d,%d]}", uuidInts[0], uuidInts[1], uuidInts[2], uuidInts[3]);
                command = command + " " + ownerTag;
            }
        }

        String summonedEntityType = getSummonedEntityType(command);
        if ("cat".equals(summonedEntityType)) {
            command = applyCatVariant(command, server);
        } else if ("horse".equals(summonedEntityType)) {
            command = applyHorseVariant(command, player);
        }

        server.getCommandManager().executeWithPrefix(player.getCommandSource().withLevel(4).withSilent(), command);
    }

    private static int[] uuidToIntArray(UUID uuid) {
        long most = uuid.getMostSignificantBits();
        long least = uuid.getLeastSignificantBits();
        return new int[]{
                (int) (most >> 32),
                (int) most,
                (int) (least >> 32),
                (int) least
        };
    }

    private static String getSummonedEntityType(String command) {
        String[] parts = command.split(" ");
        return parts.length > 1 ? parts[1] : "";
    }

    private static String applyCatVariant(String command, MinecraftServer server) {
        Optional<RegistryEntry.Reference<CatVariant>> variantEntry = Registries.CAT_VARIANT.getRandom(server.getOverworld().getRandom());
        if (variantEntry.isEmpty()) {
            return command;
        }

        String variant = Objects.requireNonNull(Registries.CAT_VARIANT.getId(variantEntry.get().value())).toString();
        return appendNbtTag(command, String.format("variant:\"%s\"", variant));
    }

    private static String applyHorseVariant(String command, ServerPlayerEntity player) {
        int color = player.getRandom().nextInt(7);
        int style = player.getRandom().nextInt(5);
        int variant = color | (style << 8);
        return appendNbtTag(command, String.format("Variant:%d", variant));
    }

    private static String appendNbtTag(String command, String tag) {
        int nbtStart = command.indexOf('{');
        if (nbtStart != -1) {
            String beforeNBT = command.substring(0, nbtStart + 1);
            String afterNBT = command.substring(nbtStart + 1);
            return afterNBT.isEmpty() ? beforeNBT + tag + "}" : beforeNBT + tag + "," + afterNBT;
        }

        return command + " {" + tag + "}";
    }
}
