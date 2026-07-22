package snowball049.roguelikemc.network.packet;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import snowball049.roguelikemc.network.RoguelikeMCNetworkConstants;

public record RefreshCurrentBossStageS2CPayload(String nextBoss) {
    public static final Identifier ID = RoguelikeMCNetworkConstants.REFRESH_CURRENT_BOSS_STAGE_PACKET_ID;


    public void write(PacketByteBuf buf) {
        buf.writeString(nextBoss);
    }

    public static RefreshCurrentBossStageS2CPayload read(PacketByteBuf buf) {
        return new RefreshCurrentBossStageS2CPayload(buf.readString());
    }
}
