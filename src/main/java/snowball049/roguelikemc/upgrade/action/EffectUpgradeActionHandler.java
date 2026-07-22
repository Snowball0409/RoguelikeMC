package snowball049.roguelikemc.upgrade.action;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import snowball049.roguelikemc.data.RoguelikeMCUpgradeData;
import snowball049.roguelikemc.upgrade.enums.UpgradeActionType;

import java.util.Objects;
import java.util.List;

public final class EffectUpgradeActionHandler implements UpgradeActionHandler {
    public static final EffectUpgradeActionHandler INSTANCE = new EffectUpgradeActionHandler();

    private EffectUpgradeActionHandler() {
    }

    @Override
    public UpgradeActionType type() {
        return UpgradeActionType.EFFECT;
    }

    @Override
    public void apply(UpgradeActionContext context) {
        applyEffect(context.player(), context.action().value());
    }

    @Override
    public void remove(UpgradeActionContext context) {
        removeEffect(context.player(), context.action().value());
    }

    @Override
    public boolean matchesTick(RoguelikeMCUpgradeData.ActionData action) {
        // Legacy normalized runtime payload. This tick check should move to semantic fields
        // when packet/runtime schema converges with authored `type + payload`.
        List<String> value = action.value();
        return value.size() > 1 && "-1".equals(value.get(1));
    }

    private static void applyEffect(net.minecraft.server.network.ServerPlayerEntity player, List<String> value) {
        // Legacy normalized runtime payload consumed by the current handler contract.
        Identifier effectIdentifier = Identifier.tryParse(value.get(0));
        StatusEffect effect = Objects.requireNonNull(Registries.STATUS_EFFECT.get(effectIdentifier));

        if (!player.getWorld().isClient()) {
            player.addStatusEffect(new StatusEffectInstance(
                    effect,
                    Integer.parseInt(value.get(1)),
                    Integer.parseInt(value.get(2)),
                    false,
                    false,
                    true
            ));
        }
    }

    private static void removeEffect(net.minecraft.server.network.ServerPlayerEntity player, List<String> value) {
        player.removeStatusEffect(Objects.requireNonNull(Registries.STATUS_EFFECT.get(Identifier.tryParse(value.get(0)))));
    }
}
