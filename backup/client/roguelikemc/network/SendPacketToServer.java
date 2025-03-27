package snowball049.roguelikemc.network;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public class SendPacketToServer {

    public static void send(CustomPayload customPayload) {
        ClientPlayNetworking.send(customPayload);
    }
}
