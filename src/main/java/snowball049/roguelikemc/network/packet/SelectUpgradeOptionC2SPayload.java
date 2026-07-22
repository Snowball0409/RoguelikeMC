package snowball049.roguelikemc.network.packet;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import snowball049.roguelikemc.network.RoguelikeMCNetworkConstants;

public record SelectUpgradeOptionC2SPayload(int optionIndex) {
    public static final Identifier ID = RoguelikeMCNetworkConstants.SEND_SELECTED_UPGRADE_PACKET_ID;


    public void write(PacketByteBuf buf) {
        buf.writeVarInt(optionIndex);
    }

    public static SelectUpgradeOptionC2SPayload read(PacketByteBuf buf) {
        return new SelectUpgradeOptionC2SPayload(buf.readVarInt());
    }
}
