package snowball049.roguelikemc.network.packet;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import snowball049.roguelikemc.config.RoguelikeMCUpgradesConfig;
import snowball049.roguelikemc.network.RoguelikeMCNetworkConstants;

public record UpgradeOptionS2CPayload(RoguelikeMCUpgradesConfig.RogueLikeMCUpgradeConfig upgrade) implements CustomPacketPayload{
    public static final CustomPacketPayload.Type<UpgradeOptionS2CPayload> ID = new CustomPacketPayload.Type<>(RoguelikeMCNetworkConstants.SEND_UPGRADE_OPTION_PACKET_ID);
    public static final StreamCodec<RegistryFriendlyByteBuf, UpgradeOptionS2CPayload> CODEC = StreamCodec.composite(RoguelikeMCUpgradesConfig.RogueLikeMCUpgradeConfig.PACKET_CODEC, UpgradeOptionS2CPayload::upgrade, UpgradeOptionS2CPayload::new);

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return new CustomPacketPayload.Type<>(RoguelikeMCNetworkConstants.SEND_UPGRADE_OPTION_PACKET_ID);
    }
}
