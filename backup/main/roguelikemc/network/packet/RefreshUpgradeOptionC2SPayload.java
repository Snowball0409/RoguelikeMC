package snowball049.roguelikemc.network.packet;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import snowball049.roguelikemc.RoguelikeMC;
import snowball049.roguelikemc.network.RoguelikeMCNetworkConstants;

public record RefreshUpgradeOptionC2SPayload() implements CustomPayload {
    public static final RefreshUpgradeOptionC2SPayload INSTANCE = new RefreshUpgradeOptionC2SPayload();
    public static final CustomPayload.Id<RefreshUpgradeOptionC2SPayload> ID = new CustomPayload.Id<>(RoguelikeMCNetworkConstants.SEND_UPGRADE_OPTION_REQUEST_PACKET_ID);
    public static final PacketCodec<RegistryByteBuf, RefreshUpgradeOptionC2SPayload> CODEC = PacketCodec.unit(RefreshUpgradeOptionC2SPayload.INSTANCE); // For Future

    @Override
    public Id<? extends CustomPayload> getId() {
        return new CustomPayload.Id<>(RoguelikeMCNetworkConstants.SEND_UPGRADE_OPTION_REQUEST_PACKET_ID);
    }
}