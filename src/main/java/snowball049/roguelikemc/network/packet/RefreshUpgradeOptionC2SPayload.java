package snowball049.roguelikemc.network.packet;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import snowball049.roguelikemc.network.RoguelikeMCNetworkConstants;

public record RefreshUpgradeOptionC2SPayload() {
    public static final RefreshUpgradeOptionC2SPayload INSTANCE = new RefreshUpgradeOptionC2SPayload();
    public static final Identifier ID = RoguelikeMCNetworkConstants.SEND_UPGRADE_OPTION_REQUEST_PACKET_ID;


    public void write(PacketByteBuf buf) {
        // Request carries no data; the channel itself is the whole message.
    }

    @SuppressWarnings("java:S1172") // buf unused, but required to mirror every other payload's read contract
    public static RefreshUpgradeOptionC2SPayload read(PacketByteBuf buf) {
        return INSTANCE;
    }
}
