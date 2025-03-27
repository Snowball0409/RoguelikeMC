package snowball049.roguelikemc.network.packet;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import snowball049.roguelikemc.config.RoguelikeMCUpgradesConfig;
import snowball049.roguelikemc.network.RoguelikeMCNetworkConstants;

public record UpgradeOptionS2CPayload(RoguelikeMCUpgradesConfig.RogueLikeMCUpgradeConfig upgrade) implements CustomPayload{
    public static final CustomPayload.Id<UpgradeOptionS2CPayload> ID = new CustomPayload.Id<>(RoguelikeMCNetworkConstants.SEND_UPGRADE_OPTION_PACKET_ID);
    public static final PacketCodec<RegistryByteBuf, UpgradeOptionS2CPayload> CODEC = PacketCodec.tuple(RoguelikeMCUpgradesConfig.RogueLikeMCUpgradeConfig.PACKET_CODEC, UpgradeOptionS2CPayload::upgrade, UpgradeOptionS2CPayload::new);

    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
        return new CustomPayload.Id<>(RoguelikeMCNetworkConstants.SEND_UPGRADE_OPTION_PACKET_ID);
    }
}
