package fr.bataillenavale.packet;

import android.util.SparseArray;

import java.io.DataInputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Set;

import fr.bataillenavale.utils.GameUtils;

/**
 * classe gerant les paquets de donnees
 */
public class PacketManager {
	/**
     * PacketManager correspond a l'instance du paquet
     */
    private static PacketManager instance;

    /**
     * attribut permettant la gestion d'un paquet a partir de son id
     */
    private SparseArray<Class<? extends Packet>> packetsByID;

    /**
     * constructeur de PacketManager
     */
    private PacketManager() {
        this.packetsByID = new SparseArray<>();
        Set<Class<? extends Packet>> packetClasses = GameUtils.getAllSubClasses(Packet.class, true);
        for (Class<? extends Packet> packetClass : packetClasses) {
            if (!packetClass.isInterface() && !Modifier.isAbstract(packetClass.getModifiers())) {
                try {
                    Field idField = packetClass.getField("ID");
                    if (Modifier.isStatic(idField.getModifiers()) && Modifier.isFinal(idField.getModifiers()) && idField.getType() == int.class) {
                        int id = idField.getInt(null);
                        if (this.packetsByID.get(id) == null) {
                            this.packetsByID.append(id, packetClass);
                        }
                    }
                } catch (NoSuchFieldException ignored) {
                } catch (IllegalAccessException ignored) {
                }
            }
        }
    }

    /**
     * getter de l'attribut instance
     */
    public static synchronized PacketManager getInstance() {
        if (instance == null) {
            instance = new PacketManager();
        }
        return instance;
    }

    /**
     * charge le paquet
     */
    public static synchronized void loadPackets() {
        if (instance == null) {
            instance = new PacketManager();
        }
    }

    /**
     * recupere le paquet
     */
    public Packet getPacket(DataInputStream inputStream) throws Exception {
        int id = inputStream.readInt();
        Class<? extends Packet> packetClass = this.packetsByID.get(id);
        if (packetClass == null) {
            throw new UnknownPacketException();
        }
        Packet packet = packetClass.newInstance();
        packet.deserialize(inputStream);
        return packet;
    }

    /**
     * getter de l'attribut packetsById
     */
    public SparseArray<Class<? extends Packet>> getPacketsByID() {
        return this.packetsByID;
    }
}
