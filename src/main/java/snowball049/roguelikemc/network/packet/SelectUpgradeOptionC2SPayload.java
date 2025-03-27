package snowball049.roguelikemc.network.packet;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import snowball049.roguelikemc.datagen.RoguelikeMCUpgradeDataProvider;
import snowball049.roguelikemc.network.RoguelikeMCNetworkConstants;

public record SelectUpgradeOptionC2SPayload(RoguelikeMCUpgradeDataProvider.RoguelikeMCUpgrade option) implements CustomPayload {
    public static final CustomPayload.Id<SelectUpgradeOptionC2SPayload> ID = new CustomPayload.Id<>(RoguelikeMCNetworkConstants.SEND_SELECTED_UPGRADE_PACKET_ID);
    public static final PacketCodec<RegistryByteBuf, SelectUpgradeOptionC2SPayload> CODEC = PacketCodec.tuple(RoguelikeMCUpgradeDataProvider.RoguelikeMCUpgrade.PACKET_CODEC, SelectUpgradeOptionC2SPayload::option, SelectUpgradeOptionC2SPayload::new);

    @Override
    public Id<? extends CustomPayload> getId() {
        return new CustomPayload.Id<>(RoguelikeMCNetworkConstants.SEND_SELECTED_UPGRADE_PACKET_ID);
    }
}
