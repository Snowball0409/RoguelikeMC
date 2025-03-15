package snowball049.roguelikemc.mixin;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import snowball049.roguelikemc.config.RoguelikeMCUpgradesConfig;
import snowball049.roguelikemc.util.RoguelikeMCUpgradeUtil;
import snowball049.roguelikemc.accessor.PlayerEntityAccessor;

import java.util.ArrayList;
import java.util.List;

// MixinPlayerEntity.java
@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin implements PlayerEntityAccessor {
    @Unique
    private final List<RoguelikeMCUpgradesConfig.RogueLikeMCUpgradeConfig> temporaryUpgrades = new ArrayList<>();
    @Unique
    private final List<RoguelikeMCUpgradesConfig.RogueLikeMCUpgradeConfig> permanentUpgrades = new ArrayList<>();

    @Unique
    public void addTemporaryUpgrades(RoguelikeMCUpgradesConfig.RogueLikeMCUpgradeConfig upgrades) {
        this.temporaryUpgrades.add(upgrades);

        if(((PlayerEntity) (Object) this) instanceof ServerPlayerEntity player) {
            RoguelikeMCUpgradeUtil.applyUpgrade(player, upgrades);
        }
    }

    @Unique
    public void addPermanentUpgrades(RoguelikeMCUpgradesConfig.RogueLikeMCUpgradeConfig upgrades) {
        this.permanentUpgrades.add(upgrades);

        if(((PlayerEntity) (Object) this) instanceof ServerPlayerEntity player) {
            RoguelikeMCUpgradeUtil.applyUpgrade(player, upgrades);
        }
    }

    @Unique
    public List<RoguelikeMCUpgradesConfig.RogueLikeMCUpgradeConfig> getTemporaryUpgrades() {
        return this.temporaryUpgrades;
    }

    @Unique
    public List<RoguelikeMCUpgradesConfig.RogueLikeMCUpgradeConfig> getPermanentUpgrades() {
        return this.permanentUpgrades;
    }

    @Unique
    public void setTemporaryUpgrades(List<RoguelikeMCUpgradesConfig.RogueLikeMCUpgradeConfig> upgrades) {
        this.temporaryUpgrades.clear();
        this.temporaryUpgrades.addAll(upgrades);
    }

    @Unique
    public void setPermanentUpgrades(List<RoguelikeMCUpgradesConfig.RogueLikeMCUpgradeConfig> upgrades) {
        this.permanentUpgrades.clear();
        this.permanentUpgrades.addAll(upgrades);
    }

}
