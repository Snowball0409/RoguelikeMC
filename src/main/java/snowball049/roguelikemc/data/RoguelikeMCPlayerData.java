package snowball049.roguelikemc.data;

import net.minecraft.util.Identifier;
import snowball049.roguelikemc.upgrade.RoguelikeMCUpgradeManager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class RoguelikeMCPlayerData {
    public List<Identifier> temporaryUpgradeIds = new ArrayList<>();
    public List<Identifier> permanentUpgradeIds = new ArrayList<>();
    public List<Identifier> currentOptionIds = new ArrayList<>(3);
    public int upgradePoints = 0;

    public int currentKillHostile = 0;
    public int currentLevelGain = 0;
    public int currentAdvancementGain = 0;
    public int currentGameStage = 0;

    public List<Identifier> activeUpgradePools = new ArrayList<>();

    // Event Upgrade
    public boolean keepEquipmentAfterDeath = false;
    public boolean revive = false;

    public RoguelikeMCPlayerData() {
    }

    public List<RoguelikeMCUpgradeData> getTemporaryUpgrades() {
        return resolveUpgrades(temporaryUpgradeIds);
    }

    public List<RoguelikeMCUpgradeData> getPermanentUpgrades() {
        return resolveUpgrades(permanentUpgradeIds);
    }

    public List<RoguelikeMCUpgradeData> getCurrentOptions() {
        return resolveUpgrades(currentOptionIds);
    }

    public Collection<RoguelikeMCUpgradeData> getAllUpgrades() {
        Collection<RoguelikeMCUpgradeData> allUpgrades = new ArrayList<>();
        allUpgrades.addAll(getTemporaryUpgrades());
        allUpgrades.addAll(getPermanentUpgrades());
        return allUpgrades;
    }

    public List<Identifier> getAllUpgradeIds() {
        List<Identifier> allUpgradeIds = new ArrayList<>();
        allUpgradeIds.addAll(temporaryUpgradeIds);
        allUpgradeIds.addAll(permanentUpgradeIds);
        return allUpgradeIds;
    }

    public void reset() {
        this.currentKillHostile = 0;
        this.currentLevelGain = 0;
        this.currentGameStage = 0;
        this.currentAdvancementGain = 0;
        this.keepEquipmentAfterDeath = false;
        this.revive = false;
    }

    private static List<RoguelikeMCUpgradeData> resolveUpgrades(List<Identifier> upgradeIds) {
        List<RoguelikeMCUpgradeData> upgrades = new ArrayList<>(upgradeIds.size());
        for (Identifier upgradeId : upgradeIds) {
            RoguelikeMCUpgradeData upgrade = RoguelikeMCUpgradeManager.getUpgrade(upgradeId);
            if (upgrade != null) {
                upgrades.add(upgrade);
            }
        }
        return upgrades;
    }
}
