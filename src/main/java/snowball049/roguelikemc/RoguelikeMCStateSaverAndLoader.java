package snowball049.roguelikemc;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.NbtString;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.World;
import snowball049.roguelikemc.data.RoguelikeMCPlayerData;
import snowball049.roguelikemc.data.RoguelikeMCUpgradeData;
import snowball049.roguelikemc.upgrade.UpgradeIds;

import java.util.*;

public class RoguelikeMCStateSaverAndLoader extends PersistentState {

    public HashMap<UUID, RoguelikeMCPlayerData> players = new HashMap<>();

    private static final Codec<List<Identifier>> UPGRADE_ID_LIST_CODEC = Identifier.CODEC.listOf();

    private static final Type<RoguelikeMCStateSaverAndLoader> type = new Type<>(
      RoguelikeMCStateSaverAndLoader::new,
      RoguelikeMCStateSaverAndLoader::createFromNbt,
            null
    );

    @Override
    public NbtCompound writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        NbtCompound playersNbt = new NbtCompound();
        players.forEach((uuid, playerData) -> {
            NbtCompound playerNbt = new NbtCompound();
            playerNbt.put("temporaryUpgrades", encodeUpgradeIdList(playerData.temporaryUpgradeIds));
            playerNbt.put("permanentUpgrades", encodeUpgradeIdList(playerData.permanentUpgradeIds));
            playerNbt.put("currentOptions", encodeUpgradeIdList(playerData.currentOptionIds));
            playerNbt.putInt("currentKillHostile", playerData.currentKillHostile);
            playerNbt.putInt("upgradePoints", playerData.upgradePoints);
            playerNbt.putInt("currentLevelGain", playerData.currentLevelGain);
            playerNbt.putInt("currentAdvancementGain", playerData.currentAdvancementGain);
            playerNbt.putInt("currentGameStage", playerData.currentGameStage);
            playerNbt.putBoolean("keepEquipmentAfterDeath", playerData.keepEquipmentAfterDeath);
            playerNbt.putBoolean("revive", playerData.revive);
            playersNbt.put(uuid.toString(), playerNbt);
        });

        nbt.put("players", playersNbt);
        return nbt;
    }

    private static RoguelikeMCStateSaverAndLoader createFromNbt(NbtCompound tag, RegistryWrapper.WrapperLookup wrapperLookup) {
        RoguelikeMCStateSaverAndLoader state = new RoguelikeMCStateSaverAndLoader();
        NbtCompound playersNbt = tag.getCompound("players");
        playersNbt.getKeys().forEach(key -> {
            RoguelikeMCPlayerData playerData = new RoguelikeMCPlayerData();
            NbtCompound playerNbt = playersNbt.getCompound(key);
            playerData.temporaryUpgradeIds = decodeUpgradeIdList(playerNbt.get("temporaryUpgrades"));
            playerData.permanentUpgradeIds = decodeUpgradeIdList(playerNbt.get("permanentUpgrades"));
            playerData.currentOptionIds = decodeUpgradeIdList(playerNbt.get("currentOptions"));
            playerData.currentKillHostile = playerNbt.getInt("currentKillHostile");
            playerData.upgradePoints = playerNbt.getInt("upgradePoints");
            playerData.currentLevelGain = playerNbt.getInt("currentLevelGain");
            playerData.currentAdvancementGain = playerNbt.getInt("currentAdvancementGain");
            playerData.currentGameStage = playerNbt.getInt("currentGameStage");
            playerData.keepEquipmentAfterDeath = playerNbt.getBoolean("keepEquipmentAfterDeath");
            playerData.revive = playerNbt.getBoolean("revive");
            state.players.put(UUID.fromString(key), playerData);
        });

        return state;
    }

    static NbtElement encodeUpgradeIdList(List<Identifier> upgradeIds) {
        return UPGRADE_ID_LIST_CODEC.encodeStart(NbtOps.INSTANCE, upgradeIds).getOrThrow();
    }

    static List<Identifier> decodeUpgradeIdList(NbtElement element) {
        if (element == null || element.getType() == NbtElement.END_TYPE) {
            return new ArrayList<>();
        }

        if (element instanceof NbtList list && !list.isEmpty() && list.get(0) instanceof NbtString) {
            return new ArrayList<>(UPGRADE_ID_LIST_CODEC
                    .decode(NbtOps.INSTANCE, element)
                    .result()
                    .map(Pair::getFirst)
                    .orElseGet(ArrayList::new));
        }

        return new ArrayList<>(RoguelikeMCUpgradeData.CODEC.listOf()
                .decode(NbtOps.INSTANCE, element)
                .result()
                .map(Pair::getFirst)
                .orElseGet(ArrayList::new)
                .stream()
                .map(UpgradeIds::fromUpgradeData)
                .toList());
    }

    public static RoguelikeMCPlayerData getPlayerState(LivingEntity player) {
        RoguelikeMCStateSaverAndLoader serverState = getServerState(Objects.requireNonNull(player.getWorld().getServer()));
        return serverState.players.computeIfAbsent(player.getUuid(), uuid -> new RoguelikeMCPlayerData());
    }

    public static RoguelikeMCStateSaverAndLoader getServerState(MinecraftServer server) {
        PersistentStateManager manager = Objects.requireNonNull(server.getWorld(World.OVERWORLD)).getPersistentStateManager();

        RoguelikeMCStateSaverAndLoader state = manager.getOrCreate(type, RoguelikeMC.MOD_ID);

        state.markDirty();
        return state;
    }
}
