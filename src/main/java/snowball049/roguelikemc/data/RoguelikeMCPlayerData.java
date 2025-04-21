package snowball049.roguelikemc.data;

import snowball049.roguelikemc.config.RoguelikeMCCommonConfig;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class RoguelikeMCPlayerData {
    public List<RoguelikeMCUpgradeData> temporaryUpgrades = new ArrayList<>();
    public List<RoguelikeMCUpgradeData> permanentUpgrades = new ArrayList<>();
    public List<RoguelikeMCUpgradeData> currentOptions = new ArrayList<>(3);
    public int upgradePoints = 0;

    public int currentKillHostile = 0;
    public int currentKillHostileRequirement;
    public int currentLevelGain = 0;
    public int currentGameStage = 0;

    // Event Upgrade
    public boolean keepEquipmentAfterDeath = false;
    public boolean revive = false;
    public float damageGainMultiplier = 1.0f;

    public RoguelikeMCPlayerData() {
        this.currentKillHostileRequirement = RoguelikeMCCommonConfig.INSTANCE.killHostileEntityRequirementMinMax.getFirst();
    }

    public Collection<RoguelikeMCUpgradeData> getAllUpgrades() {
        Collection<RoguelikeMCUpgradeData> allUpgrades = new ArrayList<>();
        allUpgrades.addAll(temporaryUpgrades);
        allUpgrades.addAll(permanentUpgrades);
        return allUpgrades;
    }

    public void reset() {
        this.currentKillHostile = 0;
        this.currentKillHostileRequirement = RoguelikeMCCommonConfig.INSTANCE.killHostileEntityRequirementMinMax.getFirst();
        this.currentLevelGain = 0;
        this.currentGameStage = 0;
        this.keepEquipmentAfterDeath = false;
        this.revive = false;
        this.damageGainMultiplier = 1.0f;
    }
}
