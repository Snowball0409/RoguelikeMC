package snowball049.roguelikemc.data;

import java.util.ArrayList;
import java.util.List;

public class RoguelikeMCClientData {
    public List<RoguelikeMCUpgradeData> temporaryUpgrades = new ArrayList<>();
    public List<RoguelikeMCUpgradeData> permanentUpgrades = new ArrayList<>();
    public List<RoguelikeMCUpgradeData> currentOptions = new ArrayList<>(3);
    public int currentPoints = 0;
    public static final RoguelikeMCClientData INSTANCE = new RoguelikeMCClientData();

}
