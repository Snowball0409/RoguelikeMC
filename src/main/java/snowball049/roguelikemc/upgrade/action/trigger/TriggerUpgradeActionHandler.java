package snowball049.roguelikemc.upgrade.action.trigger;

import snowball049.roguelikemc.RoguelikeMC;
import snowball049.roguelikemc.data.RoguelikeMCUpgradeData;
import snowball049.roguelikemc.upgrade.action.UpgradeActionContext;
import snowball049.roguelikemc.upgrade.action.UpgradeActionHandler;
import snowball049.roguelikemc.upgrade.action.UpgradeActionHandlers;
import snowball049.roguelikemc.upgrade.enums.UpgradeActionType;
import snowball049.roguelikemc.upgrade.runtime.UpgradeTriggerRuntimeService;

@SuppressWarnings("java:S6548")
public final class TriggerUpgradeActionHandler implements UpgradeActionHandler {
    public static final TriggerUpgradeActionHandler INSTANCE = new TriggerUpgradeActionHandler();

    private TriggerUpgradeActionHandler() {
    }

    @Override
    public UpgradeActionType type() {
        return UpgradeActionType.TRIGGER;
    }

    @Override
    public void apply(UpgradeActionContext context) {
        // Trigger upgrades activate through runtime hooks rather than immediate apply-time effects.
    }

    @Override
    public void remove(UpgradeActionContext context) {
        TriggerActionPayload.fromAction(context.action())
                .resultOrPartial(RoguelikeMC.LOGGER::warn)
                .ifPresent(payload -> {
                    for (RoguelikeMCUpgradeData.ActionData nestedAction : payload.actions()) {
                        UpgradeActionHandlers.remove(new UpgradeActionContext(
                                context.player(),
                                context.upgrade(),
                                nestedAction
                        ));
                    }
                });
        UpgradeTriggerRuntimeService.clearUpgrade(context.player(), context.upgradeId());
    }
}
