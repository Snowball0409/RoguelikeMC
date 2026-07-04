package snowball049.roguelikemc.upgrade.action.event;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.world.Difficulty;
import snowball049.roguelikemc.upgrade.action.UpgradeActionContext;

import java.util.List;
import java.util.Optional;

public final class ProvokedEventHandler implements UpgradeEventHandler {
    private static final int PROVOKE_RADIUS = 16;

    @Override
    public String eventType() {
        return "provoked";
    }

    @Override
    public void apply(UpgradeActionContext context) {
    }

    @Override
    public int tickInterval() {
        return 100;
    }

    @Override
    public void tick(UpgradeActionContext context) {
        if (context.player().getAbilities().creativeMode) {
            return;
        }
        if (context.player().getWorld().getDifficulty() == Difficulty.PEACEFUL) {
            return;
        }

        // Legacy normalized runtime payload: provoked event data is still positional here
        // until packet/runtime schema convergence introduces semantic runtime fields.
        List<String> value = context.action().value();
        if (value.size() < 2) {
            return;
        }

        Identifier provokedId = Identifier.tryParse(value.get(1));
        Optional<EntityType<?>> targetType = Registries.ENTITY_TYPE.getOrEmpty(provokedId);
        if (targetType.isEmpty()) {
            return;
        }

        List<MobEntity> mobs = context.player().getWorld().getEntitiesByClass(
                MobEntity.class,
                context.player().getBoundingBox().expand(PROVOKE_RADIUS),
                mob -> mob.getTarget() == null && mob.getType() == targetType.get()
        );

        for (MobEntity mob : mobs) {
            mob.setTarget(context.player());
        }
    }
}
