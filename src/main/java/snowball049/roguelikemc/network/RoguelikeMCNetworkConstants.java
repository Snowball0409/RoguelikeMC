package snowball049.roguelikemc.network;

import net.minecraft.util.Identifier;
import snowball049.roguelikemc.RoguelikeMC;

public class RoguelikeMCNetworkConstants {
    public static final Identifier SEND_UPGRADE_OPTION_PACKET_ID = Identifier.tryParse(RoguelikeMC.MOD_ID, "send_upgrade_option");
    public static final Identifier SEND_UPGRADE_TEMPORARY_PACKET_ID = Identifier.tryParse(RoguelikeMC.MOD_ID, "send_upgrade_temp");
    public static final Identifier SEND_UPGRADE_PERMANENT_PACKET_ID = Identifier.tryParse(RoguelikeMC.MOD_ID, "send_upgrade_permanent");

    public static final Identifier SEND_UPGRADE_OPTION_REQUEST_PACKET_ID = Identifier.tryParse(RoguelikeMC.MOD_ID, "send_upgrade_option_request");
}
