package snowball049.roguelikemc.upgrade.action;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import snowball049.roguelikemc.data.RoguelikeMCUpgradeData;
import snowball049.roguelikemc.upgrade.enums.UpgradeActionType;

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
        List<String> value = action.value();
        return value.size() > 1 && "-1".equals(value.get(1));
    }

    private static void applyEffect(net.minecraft.server.network.ServerPlayerEntity player, List<String> value) {
        Identifier effectIdentifier = Identifier.tryParse(value.getFirst());
        RegistryEntry.Reference<StatusEffect> effectEntry = Registries.STATUS_EFFECT.getEntry(effectIdentifier)
                .orElseThrow();

        if (!player.getWorld().isClient()) {
            player.addStatusEffect(new StatusEffectInstance(
                    effectEntry,
                    Integer.parseInt(value.get(1)),
                    Integer.parseInt(value.get(2)),
                    false,
                    false,
                    true
            ));
        }
    }

    private static void removeEffect(net.minecraft.server.network.ServerPlayerEntity player, List<String> value) {
        player.removeStatusEffect(Registries.STATUS_EFFECT.getEntry(Identifier.tryParse(value.getFirst())).orElseThrow());
    }
}
