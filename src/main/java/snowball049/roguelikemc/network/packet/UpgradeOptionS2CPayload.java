package snowball049.roguelikemc.network.packet;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import snowball049.roguelikemc.data.RoguelikeMCUpgradeData;
import snowball049.roguelikemc.network.RoguelikeMCNetworkConstants;

public record UpgradeOptionS2CPayload(RoguelikeMCUpgradeData upgrade) {
    public static final Identifier ID = RoguelikeMCNetworkConstants.SEND_UPGRADE_OPTION_PACKET_ID;


    public void write(PacketByteBuf buf) {
        upgrade.write(buf);
    }

    public static UpgradeOptionS2CPayload read(PacketByteBuf buf) {
        return new UpgradeOptionS2CPayload(RoguelikeMCUpgradeData.read(buf));
    }
}
