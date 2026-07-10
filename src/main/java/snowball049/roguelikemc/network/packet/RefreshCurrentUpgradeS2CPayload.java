package snowball049.roguelikemc.network.packet;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import snowball049.roguelikemc.data.RoguelikeMCUpgradeData;
import snowball049.roguelikemc.network.RoguelikeMCNetworkConstants;

import java.util.List;

public record RefreshCurrentUpgradeS2CPayload(boolean isPermanent, List<RoguelikeMCUpgradeData> upgrades) implements CustomPayload {
    public static final CustomPayload.Id<RefreshCurrentUpgradeS2CPayload> PACKET_ID = new CustomPayload.Id<>(RoguelikeMCNetworkConstants.REFRESH_CURRENT_UPGRADE_PACKET_ID);
    public static final PacketCodec<RegistryByteBuf, RefreshCurrentUpgradeS2CPayload> CODEC = PacketCodec.tuple(
            PacketCodecs.BOOL,
            RefreshCurrentUpgradeS2CPayload::isPermanent,
            PacketCodecs.registryCodec(RoguelikeMCUpgradeData.CODEC.listOf()),
            RefreshCurrentUpgradeS2CPayload::upgrades,
            RefreshCurrentUpgradeS2CPayload::new
    );

    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
        return PACKET_ID;
    }
}
