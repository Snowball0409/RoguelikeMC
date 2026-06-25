package snowball049.roguelikemc.upgrade.action.event;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import snowball049.roguelikemc.upgrade.action.UpgradeActionContext;

public interface UpgradeEventHandler {
    String eventType();

    void apply(UpgradeActionContext context);

    default void remove(UpgradeActionContext context) {
    }

    default void onJoin(UpgradeActionContext context) {
    }

    /**
     * Server tick interval for {@link #tick(UpgradeActionContext)}.
     * Return 0 to disable periodic ticks.
     */
    default int tickInterval() {
        return 0;
    }

    default boolean matchesPeriodicTick(UpgradeActionContext context) {
        return tickInterval() > 0;
    }

    default void tick(UpgradeActionContext context) {
        apply(context);
    }

    default boolean matchesEntityKill(UpgradeActionContext context, LivingEntity target) {
        return false;
    }

    default void onEntityKill(UpgradeActionContext context, LivingEntity target, DamageSource source) {
    }
}
