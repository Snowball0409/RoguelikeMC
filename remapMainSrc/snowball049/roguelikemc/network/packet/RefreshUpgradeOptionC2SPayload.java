package snowball049.roguelikemc.network.packet;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import snowball049.roguelikemc.RoguelikeMC;
import snowball049.roguelikemc.network.RoguelikeMCNetworkConstants;

public record RefreshUpgradeOptionC2SPayload() implements CustomPacketPayload {
    public static final RefreshUpgradeOptionC2SPayload INSTANCE = new RefreshUpgradeOptionC2SPayload();
    public static final CustomPacketPayload.Type<RefreshUpgradeOptionC2SPayload> ID = new CustomPacketPayload.Type<>(RoguelikeMCNetworkConstants.SEND_UPGRADE_OPTION_REQUEST_PACKET_ID);
    public static final StreamCodec<RegistryFriendlyByteBuf, RefreshUpgradeOptionC2SPayload> CODEC = StreamCodec.unit(RefreshUpgradeOptionC2SPayload.INSTANCE); // For Future

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return new CustomPacketPayload.Type<>(RoguelikeMCNetworkConstants.SEND_UPGRADE_OPTION_REQUEST_PACKET_ID);
    }
}