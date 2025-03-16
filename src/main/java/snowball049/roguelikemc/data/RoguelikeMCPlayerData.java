package snowball049.roguelikemc.data;

import snowball049.roguelikemc.RoguelikeMC;
import snowball049.roguelikemc.config.RoguelikeMCCommonConfig;
import snowball049.roguelikemc.config.RoguelikeMCUpgradesConfig;

import java.util.ArrayList;
import java.util.List;

public class RoguelikeMCPlayerData {
    public List<RoguelikeMCUpgradesConfig.RogueLikeMCUpgradeConfig> temporaryUpgrades = new ArrayList<>();
    public List<RoguelikeMCUpgradesConfig.RogueLikeMCUpgradeConfig> permanentUpgrades = new ArrayList<>();
    public int currentKillHostile = 0;
    public int currentKillHostileRequirement = 0;
    public int upgradePoints = 0;

    public RoguelikeMCPlayerData() {
        this.currentKillHostileRequirement = RoguelikeMCCommonConfig.INSTANCE.killHostileEntityRequirementMinMax.getFirst();
    }
}
