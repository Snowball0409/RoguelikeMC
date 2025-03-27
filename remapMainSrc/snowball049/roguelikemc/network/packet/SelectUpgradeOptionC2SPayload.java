package snowball049.roguelikemc.network.packet;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import snowball049.roguelikemc.config.RoguelikeMCUpgradesConfig;
import snowball049.roguelikemc.network.RoguelikeMCNetworkConstants;

public record SelectUpgradeOptionC2SPayload(RoguelikeMCUpgradesConfig.RogueLikeMCUpgradeConfig option) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<SelectUpgradeOptionC2SPayload> ID = new CustomPacketPayload.Type<>(RoguelikeMCNetworkConstants.SEND_SELECTED_UPGRADE_PACKET_ID);
    public static final StreamCodec<RegistryFriendlyByteBuf, SelectUpgradeOptionC2SPayload> CODEC = StreamCodec.composite(RoguelikeMCUpgradesConfig.RogueLikeMCUpgradeConfig.PACKET_CODEC, SelectUpgradeOptionC2SPayload::option, SelectUpgradeOptionC2SPayload::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return new CustomPacketPayload.Type<>(RoguelikeMCNetworkConstants.SEND_SELECTED_UPGRADE_PACKET_ID);
    }
}
