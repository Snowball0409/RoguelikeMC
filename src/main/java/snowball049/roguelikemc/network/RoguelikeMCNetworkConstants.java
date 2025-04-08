package snowball049.roguelikemc.network;

import net.minecraft.util.Identifier;
import snowball049.roguelikemc.RoguelikeMC;

public class RoguelikeMCNetworkConstants {
    public static final Identifier SEND_UPGRADE_OPTION_PACKET_ID = Identifier.tryParse(RoguelikeMC.MOD_ID, "send_upgrade_option");
    public static final Identifier SEND_SELECTED_UPGRADE_PACKET_ID = Identifier.tryParse(RoguelikeMC.MOD_ID, "send_selected_upgrade");
    public static final Identifier REFRESH_CURRENT_UPGRADE_PACKET_ID = Identifier.tryParse(RoguelikeMC.MOD_ID, "refresh_current_upgrade");

    public static final Identifier SEND_UPGRADE_OPTION_REQUEST_PACKET_ID = Identifier.tryParse(RoguelikeMC.MOD_ID, "send_upgrade_option_request");
    public static final Identifier SEND_UPGRADE_POINTS_PACKET_ID = Identifier.tryParse(RoguelikeMC.MOD_ID, "send_upgrade_points");
}
