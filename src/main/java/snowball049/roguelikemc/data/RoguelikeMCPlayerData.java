package snowball049.roguelikemc.data;

import snowball049.roguelikemc.config.RoguelikeMCConfig;

import java.util.ArrayList;
import java.util.List;

public class RoguelikeMCPlayerData {
    public List<RoguelikeMCConfig.RogueLikeMCUpgradeConfig> temporaryUpgrades = new ArrayList<>();
    public List<RoguelikeMCConfig.RogueLikeMCUpgradeConfig> permanentUpgrades = new ArrayList<>();
}
