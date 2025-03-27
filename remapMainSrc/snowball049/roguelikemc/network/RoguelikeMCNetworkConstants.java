package snowball049.roguelikemc.network;

import net.minecraft.resources.ResourceLocation;
import snowball049.roguelikemc.RoguelikeMC;

public class RoguelikeMCNetworkConstants {
    public static final ResourceLocation SEND_UPGRADE_OPTION_PACKET_ID = ResourceLocation.tryBuild(RoguelikeMC.MOD_ID, "send_upgrade_option");
    public static final ResourceLocation SEND_SELECTED_UPGRADE_PACKET_ID = ResourceLocation.tryBuild(RoguelikeMC.MOD_ID, "send_selected_upgrade");
    public static final ResourceLocation REFRESH_CURRENT_UPGRADE_PACKET_ID = ResourceLocation.tryBuild(RoguelikeMC.MOD_ID, "refresh_current_upgrade");

    public static final ResourceLocation SEND_UPGRADE_OPTION_REQUEST_PACKET_ID = ResourceLocation.tryBuild(RoguelikeMC.MOD_ID, "send_upgrade_option_request");
}
