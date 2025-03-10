package snowball049.roguelikemc.network;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.util.Identifier;
import snowball049.roguelikemc.gui.RoguelikeMCScreen;

public class RoguelikeMCUpgradePacket {
//    public static final Identifier PACKET_ID = Identifier.tryParse("roguelikemc", "upgrade");
//
//    public static void register() {
//        assert PACKET_ID != null;
//        ServerPlayNetworking.registerGlobalReceiver(PACKET_ID,
//                (server, player, handler, buf, responseSender) -> {
//                    String upgradeId = buf.readString();
//                    server.execute(() -> {
//                        UpgradeApplier.applyUpgrade(player, upgradeId);
//                    });
//                });
//    }

    public static boolean sendUpgradeToServer(RoguelikeMCScreen.UpgradeEffect upgradeId) {
//        PacketByteBuf buf = PacketByteBufs.create();
//        buf.writeString(upgradeId);
//        ClientPlayNetworking.send(PACKET_ID, buf);
        return true;
    }
}
