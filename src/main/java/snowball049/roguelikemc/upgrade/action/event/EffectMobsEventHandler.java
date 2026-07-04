package snowball049.roguelikemc.upgrade.action.event;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import snowball049.roguelikemc.RoguelikeMC;
import snowball049.roguelikemc.upgrade.action.UpgradeActionContext;

import java.util.List;

public final class EffectMobsEventHandler implements UpgradeEventHandler {
    @Override
    public String eventType() {
        return "effect_mobs";
    }

    @Override
    public void apply(UpgradeActionContext context) {
        // Legacy normalized runtime payload: authored `payload` fields are still routed
        // through value[] until runtime schema convergence happens.
        List<String> value = context.action().value();
        try {
            Identifier effectIdentifier = Identifier.tryParse(value.get(1));
            RegistryEntry.Reference<StatusEffect> effectEntry = Registries.STATUS_EFFECT.getEntry(effectIdentifier)
                    .orElseThrow();
            World world = context.player().getWorld();
            if (world.isClient()) {
                return;
            }
            Box area = new Box(context.player().getBlockPos()).expand(Double.parseDouble(value.get(3)));
            for (LivingEntity entity : world.getEntitiesByClass(
                    LivingEntity.class,
                    area,
                    candidate -> !candidate.isPlayer() && candidate instanceof HostileEntity
            )) {
                entity.addStatusEffect(new StatusEffectInstance(
                        effectEntry,
                        40,
                        Integer.parseInt(value.get(2)),
                        false,
                        true,
                        false
                ));
            }
        } catch (Exception e) {
            RoguelikeMC.LOGGER.warn("{}:{}", e.getClass(), e.getMessage());
        }
    }

    @Override
    public int tickInterval() {
        return 20;
    }
}
