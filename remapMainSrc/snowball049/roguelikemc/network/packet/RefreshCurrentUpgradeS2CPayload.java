package snowball049.roguelikemc.network.packet;

import snowball049.roguelikemc.config.RoguelikeMCUpgradesConfig;
import snowball049.roguelikemc.network.RoguelikeMCNetworkConstants;

import java.util.List;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record RefreshCurrentUpgradeS2CPayload(List<RoguelikeMCUpgradesConfig.RogueLikeMCUpgradeConfig> upgrades) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<RefreshCurrentUpgradeS2CPayload> ID = new CustomPacketPayload.Type<>(RoguelikeMCNetworkConstants.REFRESH_CURRENT_UPGRADE_PACKET_ID);
    public static final StreamCodec<RegistryFriendlyByteBuf, RefreshCurrentUpgradeS2CPayload> CODEC = StreamCodec.composite(ByteBufCodecs.fromCodecWithRegistries(RoguelikeMCUpgradesConfig.RogueLikeMCUpgradeConfig.CODEC.listOf()), RefreshCurrentUpgradeS2CPayload::upgrades, RefreshCurrentUpgradeS2CPayload::new);

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return new CustomPacketPayload.Type<>(RoguelikeMCNetworkConstants.REFRESH_CURRENT_UPGRADE_PACKET_ID);
    }
}
