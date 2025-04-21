package snowball049.roguelikemc.data;

import java.util.ArrayList;
import java.util.List;

public class RoguelikeMCClientData {
    private List<RoguelikeMCUpgradeData> temporary_effects = new ArrayList<>();
    private List<RoguelikeMCUpgradeData> permanent_effects = new ArrayList<>();
    public final List<RoguelikeMCUpgradeData> currentOptions = new ArrayList<>(3);
    private int currentPoints = 0;

    public int getCurrentPoints() {
        return currentPoints;
    }

    public void setCurrentPoints(int currentPoints) {
        this.currentPoints = currentPoints;
    }

    public List<RoguelikeMCUpgradeData> getTemporary_effects() {
        return temporary_effects;
    }

    public void setTemporary_effects(List<RoguelikeMCUpgradeData> temporary_effects) {
        this.temporary_effects = temporary_effects;
    }

    public List<RoguelikeMCUpgradeData> getPermanent_effects() {
        return permanent_effects;
    }

    public void setPermanent_effects(List<RoguelikeMCUpgradeData> permanent_effects) {
        this.permanent_effects = permanent_effects;
    }
}
