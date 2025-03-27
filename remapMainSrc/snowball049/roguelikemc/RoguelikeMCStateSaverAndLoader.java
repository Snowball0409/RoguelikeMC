package snowball049.roguelikemc;

import com.mojang.datafixers.util.Pair;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;
import snowball049.roguelikemc.config.RoguelikeMCUpgradesConfig;
import snowball049.roguelikemc.data.RoguelikeMCPlayerData;

import java.util.*;

public class RoguelikeMCStateSaverAndLoader extends SavedData {

    public HashMap<UUID, RoguelikeMCPlayerData> players = new HashMap<>();

    private static final Factory<RoguelikeMCStateSaverAndLoader> type = new Factory<>(
      RoguelikeMCStateSaverAndLoader::new,
      RoguelikeMCStateSaverAndLoader::createFromNbt,
            null
    );

    @Override
    public CompoundTag save(CompoundTag nbt, HolderLookup.Provider registryLookup) {
        CompoundTag playersNbt = new CompoundTag();
        players.forEach((uuid, playerData) -> {
            CompoundTag playerNbt = new CompoundTag();
            playerNbt.put("temporaryUpgrades", RoguelikeMCUpgradesConfig.RogueLikeMCUpgradeConfig.CODEC.listOf().encodeStart(NbtOps.INSTANCE, playerData.temporaryUpgrades).getOrThrow());
            playerNbt.put("permanentUpgrades", RoguelikeMCUpgradesConfig.RogueLikeMCUpgradeConfig.CODEC.listOf().encodeStart(NbtOps.INSTANCE, playerData.permanentUpgrades).getOrThrow());
            playerNbt.putInt("currentKillHostile", playerData.currentKillHostile);
            playerNbt.putInt("currentKillHostileRequirement", playerData.currentKillHostileRequirement);
            playerNbt.putInt("upgradePoints", playerData.upgradePoints);
            playersNbt.put(uuid.toString(), playerNbt);
        });

        nbt.put("players", playersNbt);
        return nbt;
    }

    private static RoguelikeMCStateSaverAndLoader createFromNbt(CompoundTag tag, HolderLookup.Provider wrapperLookup) {
        RoguelikeMCStateSaverAndLoader state = new RoguelikeMCStateSaverAndLoader();
        CompoundTag playersNbt = tag.getCompound("players");
        playersNbt.getAllKeys().forEach(key -> {
            RoguelikeMCPlayerData playerData = new RoguelikeMCPlayerData();
            CompoundTag playerNbt = playersNbt.getCompound(key);
            Tag tempElement = playerNbt.get("temporaryUpgrades");
            Tag permElement = playerNbt.get("permanentUpgrades");
            playerData.temporaryUpgrades = new ArrayList<>(RoguelikeMCUpgradesConfig.RogueLikeMCUpgradeConfig.CODEC.listOf().decode(NbtOps.INSTANCE, tempElement).result().map(Pair::getFirst).orElse(new ArrayList<>()));
            playerData.permanentUpgrades = new ArrayList<>(RoguelikeMCUpgradesConfig.RogueLikeMCUpgradeConfig.CODEC.listOf().decode(NbtOps.INSTANCE, permElement).result().map(Pair::getFirst).orElse(new ArrayList<>()));
            playerData.currentKillHostile = playerNbt.getInt("currentKillHostile");
            playerData.currentKillHostileRequirement = playerNbt.getInt("currentKillHostileRequirement");
            playerData.upgradePoints = playerNbt.getInt("upgradePoints");
            state.players.put(UUID.fromString(key), playerData);
        });

        return state;
    }

    public static RoguelikeMCPlayerData getPlayerState(LivingEntity player) {
        RoguelikeMCStateSaverAndLoader serverState = getServerState(Objects.requireNonNull(player.level().getServer()));
        return serverState.players.computeIfAbsent(player.getUUID(), uuid -> new RoguelikeMCPlayerData());
    }

    public static RoguelikeMCStateSaverAndLoader getServerState(MinecraftServer server) {
        DimensionDataStorage manager = Objects.requireNonNull(server.getLevel(Level.OVERWORLD)).getDataStorage();

        RoguelikeMCStateSaverAndLoader state = manager.computeIfAbsent(type, RoguelikeMC.MOD_ID);

        state.setDirty();
        return state;
    }
}
