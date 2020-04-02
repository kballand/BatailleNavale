package fr.bataillenavale.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.UUID;

public class PlayerHasLostPacket extends Packet {
    public final static int ID = 19;

    private UUID playerUUID;

    public PlayerHasLostPacket() {
    }

    public PlayerHasLostPacket(UUID playerUUID) {
        this.playerUUID = playerUUID;
    }

    @Override
    public int getID() {
        return ID;
    }

    public UUID getPlayerUUID() {
        return this.playerUUID;
    }

    @Override
    public void serialize(DataOutputStream outputStream) throws IOException {
        super.serialize(outputStream);
        outputStream.writeUTF(playerUUID.toString());
    }

    @Override
    public void deserialize(DataInputStream inputStream) throws IOException {
        this.playerUUID = UUID.fromString(inputStream.readUTF());
    }
}
