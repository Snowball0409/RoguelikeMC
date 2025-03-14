package snowball049.roguelikemc.mixin;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import snowball049.roguelikemc.config.RoguelikeMCConfig;
import snowball049.roguelikemc.network.packet.RefreshCurrentUpgradeS2CPayload;
import snowball049.roguelikemc.util.RoguelikeMCUpgradeUtil;
import snowball049.roguelikemc.accessor.PlayerEntityAccessor;

import java.util.ArrayList;
import java.util.List;

// MixinPlayerEntity.java
@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin implements PlayerEntityAccessor {
    @Unique
    private final List<RoguelikeMCConfig.RogueLikeMCUpgradeConfig> temporaryUpgrades = new ArrayList<>();
    @Unique
    private final List<RoguelikeMCConfig.RogueLikeMCUpgradeConfig> permanentUpgrades = new ArrayList<>();

    @Unique
    public void addTemporaryUpgrades(RoguelikeMCConfig.RogueLikeMCUpgradeConfig upgrades) {
        this.temporaryUpgrades.add(upgrades);

        if(((PlayerEntity) (Object) this) instanceof ServerPlayerEntity player) {
            RoguelikeMCUpgradeUtil.applyUpgrade(player, upgrades);
        }
    }

    @Unique
    public void addPermanentUpgrades(RoguelikeMCConfig.RogueLikeMCUpgradeConfig upgrades) {
        this.permanentUpgrades.add(upgrades);

        if(((PlayerEntity) (Object) this) instanceof ServerPlayerEntity player) {
            RoguelikeMCUpgradeUtil.applyUpgrade(player, upgrades);
        }
    }

    @Unique
    public List<RoguelikeMCConfig.RogueLikeMCUpgradeConfig> getTemporaryUpgrades() {
        return this.temporaryUpgrades;
    }

    @Unique
    public List<RoguelikeMCConfig.RogueLikeMCUpgradeConfig> getPermanentUpgrades() {
        return this.permanentUpgrades;
    }

    @Unique
    public void setTemporaryUpgrades(List<RoguelikeMCConfig.RogueLikeMCUpgradeConfig> upgrades) {
        this.temporaryUpgrades.clear();
        this.temporaryUpgrades.addAll(upgrades);
    }

    @Unique
    public void setPermanentUpgrades(List<RoguelikeMCConfig.RogueLikeMCUpgradeConfig> upgrades) {
        this.permanentUpgrades.clear();
        this.permanentUpgrades.addAll(upgrades);
    }

}
