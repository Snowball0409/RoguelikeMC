package snowball049.roguelikemc.upgrade.action.event;

import snowball049.roguelikemc.upgrade.action.UpgradeActionContext;

public final class AllowCreativeFlyingEventHandler implements UpgradeEventHandler {
    @Override
    public String eventType() {
        return "allow_creative_flying";
    }

    @Override
    public void apply(UpgradeActionContext context) {
        context.player().getAbilities().allowFlying = true;
        context.player().sendAbilitiesUpdate();
    }

    @Override
    public void remove(UpgradeActionContext context) {
        context.player().getAbilities().allowFlying = false;
        context.player().sendAbilitiesUpdate();
    }

    @Override
    public void onJoin(UpgradeActionContext context) {
        context.player().getAbilities().allowFlying = true;
        if (!context.player().isOnGround()) {
            context.player().getAbilities().flying = true;
        }
        context.player().sendAbilitiesUpdate();
    }

    @Override
    public int tickInterval() {
        return 20;
    }
}
