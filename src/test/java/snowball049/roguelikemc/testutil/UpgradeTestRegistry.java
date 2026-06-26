package snowball049.roguelikemc.testutil;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import net.minecraft.util.Identifier;
import snowball049.roguelikemc.data.RoguelikeMCUpgradeData;
import snowball049.roguelikemc.upgrade.RoguelikeMCUpgradeManager;
import snowball049.roguelikemc.upgrade.RoguelikeMCUpgradePoolManager;
import snowball049.roguelikemc.upgrade.UpgradeIds;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class UpgradeTestRegistry {
    private UpgradeTestRegistry() {
    }

    public static void clear() {
        RoguelikeMCUpgradeManager.replaceUpgradesForTesting(Map.of());
        RoguelikeMCUpgradePoolManager.replacePoolsForTesting(Map.of());
    }

    public static void loadRegistry() {
        loadRegistrySections("shared", "roll");
    }

    public static void loadRollRegistry() {
        loadRegistrySections("roll");
    }

    public static void loadRegistrySections(String... sections) {
        JsonObject registry = TestFixtures.root(TestFixtures.UPGRADES).getAsJsonObject("registry");
        Map<Identifier, RoguelikeMCUpgradeData> upgrades = new HashMap<>();
        for (String section : sections) {
            JsonObject sectionRegistry = registry.getAsJsonObject(section);
            for (String key : sectionRegistry.keySet()) {
                RoguelikeMCUpgradeData upgrade = parseUpgrade(sectionRegistry.getAsJsonObject(key));
                upgrades.put(UpgradeIds.fromUpgradeData(upgrade), upgrade);
            }
        }
        RoguelikeMCUpgradeManager.replaceUpgradesForTesting(upgrades);
    }

    public static void loadPools() {
        JsonObject pools = TestFixtures.object(TestFixtures.GAMEPLAY, "pools");
        Map<Identifier, List<Identifier>> poolMap = new HashMap<>();
        for (String key : pools.keySet()) {
            JsonObject pool = pools.getAsJsonObject(key);
            Identifier poolId = Identifier.of(pool.get("id").getAsString());
            List<Identifier> upgradeIds = readIdentifierList(pool.getAsJsonArray("upgradeIds"));
            poolMap.put(poolId, upgradeIds);
        }
        RoguelikeMCUpgradePoolManager.replacePoolsForTesting(poolMap);
    }

    public static RoguelikeMCUpgradeData registryUpgrade(String registryKey) {
        JsonObject registry = TestFixtures.root(TestFixtures.UPGRADES).getAsJsonObject("registry");
        JsonObject shared = registry.getAsJsonObject("shared");
        if (shared.has(registryKey)) {
            return parseUpgrade(shared.getAsJsonObject(registryKey));
        }
        return parseUpgrade(registry.getAsJsonObject("roll").getAsJsonObject(registryKey));
    }

    public static RoguelikeMCUpgradeData parseUpgrade(JsonObject json) {
        return RoguelikeMCUpgradeData.CODEC.parse(JsonOps.INSTANCE, json).getOrThrow();
    }

    public static List<Identifier> readIdentifierList(JsonArray array) {
        List<Identifier> identifiers = new ArrayList<>();
        for (JsonElement element : array) {
            identifiers.add(Identifier.of(element.getAsString()));
        }
        return identifiers;
    }
}
