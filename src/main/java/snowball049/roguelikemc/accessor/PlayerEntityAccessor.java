package snowball049.roguelikemc.accessor;

import snowball049.roguelikemc.config.RoguelikeMCConfig;

import java.util.List;

public interface PlayerEntityAccessor {
    void addTemporaryUpgrades(RoguelikeMCConfig.RogueLikeMCUpgradeConfig upgrades);
    void addPermanentUpgrades(RoguelikeMCConfig.RogueLikeMCUpgradeConfig upgrades);
    void setTemporaryUpgrades(List<RoguelikeMCConfig.RogueLikeMCUpgradeConfig> upgrades);
    void setPermanentUpgrades(List<RoguelikeMCConfig.RogueLikeMCUpgradeConfig> upgrades);
    List<RoguelikeMCConfig.RogueLikeMCUpgradeConfig> getTemporaryUpgrades();
    List<RoguelikeMCConfig.RogueLikeMCUpgradeConfig> getPermanentUpgrades();
}
