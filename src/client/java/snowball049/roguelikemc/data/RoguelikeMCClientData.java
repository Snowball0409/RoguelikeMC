package snowball049.roguelikemc.data;

import net.minecraft.util.Identifier;
import snowball049.roguelikemc.RoguelikeMC;

import java.util.ArrayList;
import java.util.List;

public class RoguelikeMCClientData {
    public List<RoguelikeMCUpgradeData> temporaryUpgrades = new ArrayList<>();
    public List<RoguelikeMCUpgradeData> permanentUpgrades = new ArrayList<>();
    public List<RoguelikeMCUpgradeData> currentOptions = new ArrayList<>(3);
    public int currentPoints = 0;
    public static final RoguelikeMCClientData INSTANCE = new RoguelikeMCClientData();
    public Identifier nextBoss = Identifier.of(RoguelikeMC.MOD_ID, "none");

}
