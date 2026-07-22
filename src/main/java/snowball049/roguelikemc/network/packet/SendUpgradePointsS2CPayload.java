package snowball049.roguelikemc.network.packet;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import snowball049.roguelikemc.network.RoguelikeMCNetworkConstants;

public record SendUpgradePointsS2CPayload(int point) {
    public static final Identifier ID = RoguelikeMCNetworkConstants.SEND_UPGRADE_POINTS_PACKET_ID;


    public void write(PacketByteBuf buf) {
        buf.writeVarInt(point);
    }

    public static SendUpgradePointsS2CPayload read(PacketByteBuf buf) {
        return new SendUpgradePointsS2CPayload(buf.readVarInt());
    }
}
