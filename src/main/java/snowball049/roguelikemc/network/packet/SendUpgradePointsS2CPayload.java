package snowball049.roguelikemc.network.packet;

import com.mojang.serialization.Codec;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import snowball049.roguelikemc.network.RoguelikeMCNetworkConstants;

public record SendUpgradePointsS2CPayload(int point) implements CustomPayload {
    public static final CustomPayload.Id<SendUpgradePointsS2CPayload> ID = new CustomPayload.Id<>(RoguelikeMCNetworkConstants.SEND_UPGRADE_POINTS_PACKET_ID);
    public static final PacketCodec<RegistryByteBuf, SendUpgradePointsS2CPayload> CODEC = PacketCodec.tuple(
            PacketCodecs.INTEGER,
            SendUpgradePointsS2CPayload::point,
            SendUpgradePointsS2CPayload::new
    );

    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
        return ID;
    }
}
