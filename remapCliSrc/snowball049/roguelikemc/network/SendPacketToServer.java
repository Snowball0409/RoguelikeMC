package snowball049.roguelikemc.network;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public class SendPacketToServer {

    public static void send(CustomPacketPayload customPayload) {
        ClientPlayNetworking.send(customPayload);
    }
}
