package snowball049.roguelikemc.data;

import snowball049.roguelikemc.config.RoguelikeMCUpgradesConfig;

import java.util.ArrayList;
import java.util.List;

public class RoguelikeMCPlayerData {
    public List<RoguelikeMCUpgradesConfig.RogueLikeMCUpgradeConfig> temporaryUpgrades = new ArrayList<>();
    public List<RoguelikeMCUpgradesConfig.RogueLikeMCUpgradeConfig> permanentUpgrades = new ArrayList<>();
}
