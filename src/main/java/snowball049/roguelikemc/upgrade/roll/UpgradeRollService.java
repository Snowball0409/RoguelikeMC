package snowball049.roguelikemc.upgrade.roll;

import snowball049.roguelikemc.data.RoguelikeMCPlayerData;
import snowball049.roguelikemc.data.RoguelikeMCUpgradeData;
import snowball049.roguelikemc.upgrade.RoguelikeMCUpgradeManager;
import snowball049.roguelikemc.upgrade.RoguelikeMCUpgradePoolManager;
import snowball049.roguelikemc.upgrade.enums.UpgradeStacking;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

public final class UpgradeRollService {
    private static final int OPTION_COUNT = 3;
    private static final int MAX_ROLL_ATTEMPTS = 1000;

    private UpgradeRollService() {
    }

    public static List<RoguelikeMCUpgradeData> rollOptions(RoguelikeMCPlayerData playerData) {
        List<RoguelikeMCUpgradeData> candidatePool = resolveCandidatePool(playerData);
        List<RoguelikeMCUpgradeData> available = filterAvailableUpgrades(playerData, candidatePool);

        if (available.isEmpty()) {
            return List.of();
        }

        List<RoguelikeMCUpgradeData> weightedPool = buildWeightedPool(available);
        List<RoguelikeMCUpgradeData> chosen = pickUniqueOptions(weightedPool);
        ensureAtLeastOneStackableOption(chosen, available);
        return chosen;
    }

    private static List<RoguelikeMCUpgradeData> resolveCandidatePool(RoguelikeMCPlayerData playerData) {
        List<RoguelikeMCUpgradeData> poolUpgrades = playerData.activeUpgradePools.stream()
                .flatMap(poolId -> RoguelikeMCUpgradePoolManager.getUpgradesFromPool(poolId).stream())
                .distinct()
                .map(RoguelikeMCUpgradeManager::getUpgrade)
                .filter(Objects::nonNull)
                .toList();

        if (!poolUpgrades.isEmpty()) {
            return poolUpgrades;
        }

        return RoguelikeMCUpgradeManager.getUpgrades().stream().toList();
    }

    private static List<RoguelikeMCUpgradeData> filterAvailableUpgrades(
            RoguelikeMCPlayerData playerData,
            List<RoguelikeMCUpgradeData> candidatePool
    ) {
        Set<String> ownedUniqueIds = playerData.getAllUpgrades().stream()
                .filter(upgrade -> upgrade.stacking() == UpgradeStacking.UNIQUE)
                .map(RoguelikeMCUpgradeData::id)
                .collect(Collectors.toSet());

        return candidatePool.stream()
                .filter(upgrade -> !(upgrade.stacking() == UpgradeStacking.UNIQUE && ownedUniqueIds.contains(upgrade.id())))
                .toList();
    }

    private static List<RoguelikeMCUpgradeData> buildWeightedPool(List<RoguelikeMCUpgradeData> available) {
        List<RoguelikeMCUpgradeData> weightedPool = new ArrayList<>();
        for (RoguelikeMCUpgradeData upgrade : available) {
            int weight = upgrade.rarity().rollWeight();
            for (int i = 0; i < weight; i++) {
                weightedPool.add(upgrade);
            }
        }
        return weightedPool;
    }

    private static List<RoguelikeMCUpgradeData> pickUniqueOptions(List<RoguelikeMCUpgradeData> weightedPool) {
        List<RoguelikeMCUpgradeData> chosen = new ArrayList<>();
        Set<String> selectedIds = new HashSet<>();
        Random random = new Random();

        int tries = 0;
        while (chosen.size() < OPTION_COUNT && tries < MAX_ROLL_ATTEMPTS && !weightedPool.isEmpty()) {
            tries++;
            RoguelikeMCUpgradeData candidate = weightedPool.get(random.nextInt(weightedPool.size()));
            if (selectedIds.contains(candidate.id())) {
                continue;
            }
            chosen.add(candidate);
            selectedIds.add(candidate.id());
        }

        return chosen;
    }

    private static void ensureAtLeastOneStackableOption(
            List<RoguelikeMCUpgradeData> chosen,
            List<RoguelikeMCUpgradeData> available
    ) {
        if (chosen.isEmpty()) {
            return;
        }

        if (chosen.stream().anyMatch(upgrade -> upgrade.stacking() == UpgradeStacking.STACKABLE)) {
            return;
        }

        Set<String> selectedIds = chosen.stream()
                .map(RoguelikeMCUpgradeData::id)
                .collect(Collectors.toSet());
        List<RoguelikeMCUpgradeData> stackablePool = available.stream()
                .filter(upgrade -> upgrade.stacking() == UpgradeStacking.STACKABLE && !selectedIds.contains(upgrade.id()))
                .toList();

        if (!stackablePool.isEmpty()) {
            chosen.set(0, stackablePool.get(new Random().nextInt(stackablePool.size())));
        }
    }
}
