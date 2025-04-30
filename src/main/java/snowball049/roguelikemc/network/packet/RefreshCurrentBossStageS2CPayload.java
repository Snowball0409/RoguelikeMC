package snowball049.roguelikemc.network.packet;


import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import snowball049.roguelikemc.network.RoguelikeMCNetworkConstants;

public record RefreshCurrentBossStageS2CPayload (String nextBoss) implements CustomPayload {
    public static final CustomPayload.Id<RefreshCurrentBossStageS2CPayload> ID = new CustomPayload.Id<>(RoguelikeMCNetworkConstants.REFRESH_CURRENT_BOSS_STAGE_PACKET_ID);
    public static final PacketCodec<RegistryByteBuf, RefreshCurrentBossStageS2CPayload> CODEC = PacketCodec.tuple(
            PacketCodecs.STRING,
            RefreshCurrentBossStageS2CPayload::nextBoss,
            RefreshCurrentBossStageS2CPayload::new
    );

    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
        return ID;
    }
}
