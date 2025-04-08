package snowball049.roguelikemc.network.packet;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import snowball049.roguelikemc.data.RoguelikeMCUpgradeData;
import snowball049.roguelikemc.network.RoguelikeMCNetworkConstants;

public record UpgradeOptionS2CPayload(RoguelikeMCUpgradeData upgrade) implements CustomPayload{
    public static final CustomPayload.Id<UpgradeOptionS2CPayload> ID = new CustomPayload.Id<>(RoguelikeMCNetworkConstants.SEND_UPGRADE_OPTION_PACKET_ID);
    public static final PacketCodec<RegistryByteBuf, UpgradeOptionS2CPayload> CODEC = PacketCodec.tuple(RoguelikeMCUpgradeData.PACKET_CODEC, UpgradeOptionS2CPayload::upgrade, UpgradeOptionS2CPayload::new);

    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
        return ID;
    }
}
