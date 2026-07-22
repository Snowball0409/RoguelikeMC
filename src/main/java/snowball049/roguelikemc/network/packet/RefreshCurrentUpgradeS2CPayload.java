package snowball049.roguelikemc.network.packet;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import snowball049.roguelikemc.data.RoguelikeMCUpgradeData;
import snowball049.roguelikemc.network.RoguelikeMCNetworkConstants;

import java.util.List;

public record RefreshCurrentUpgradeS2CPayload(boolean isPermanent, List<RoguelikeMCUpgradeData> upgrades) {
    public static final Identifier ID = RoguelikeMCNetworkConstants.REFRESH_CURRENT_UPGRADE_PACKET_ID;


    public void write(PacketByteBuf buf) {
        buf.writeBoolean(isPermanent);
        buf.writeCollection(upgrades, (b, upgrade) -> upgrade.write(b));
    }

    public static RefreshCurrentUpgradeS2CPayload read(PacketByteBuf buf) {
        boolean isPermanent = buf.readBoolean();
        List<RoguelikeMCUpgradeData> upgrades = buf.readList(RoguelikeMCUpgradeData::read);
        return new RefreshCurrentUpgradeS2CPayload(isPermanent, upgrades);
    }
}
