package snowball049.roguelikemc.upgrade.action;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import snowball049.roguelikemc.RoguelikeMC;
import snowball049.roguelikemc.data.RoguelikeMCUpgradeData;
import snowball049.roguelikemc.upgrade.enums.UpgradePersistence;

public record UpgradeActionContext(
        ServerPlayerEntity player,
        RoguelikeMCUpgradeData upgrade,
        RoguelikeMCUpgradeData.ActionData action
) {
    public boolean isPermanent() {
        return upgrade.persistence() == UpgradePersistence.PERMANENT;
    }

    public Identifier upgradeId() {
        return Identifier.of(RoguelikeMC.MOD_ID, upgrade.id());
    }
}
