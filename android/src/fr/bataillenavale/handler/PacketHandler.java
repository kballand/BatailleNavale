package fr.bataillenavale.handler;

import android.util.SparseArray;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Set;

import fr.bataillenavale.connection.ServerSenderReceiver;
import fr.bataillenavale.packet.Packet;
import fr.bataillenavale.packet.PacketManager;
import fr.bataillenavale.utils.GameUtils;

/**
 * Classe permettant de gérer la reception des packets et
 * appelant les méthodes lui étant associées
 *
 * @author Killian BALLAND, Louis MATUCHET, Matthieu LABALETTE, Thibaut ROUSCHMEYER, Etienne GIBIAT
 */
public class PacketHandler {

    // Instance unique du gestionnaire
    private static PacketHandler instance;
    // Association des méthodes correspondant à un packet identifiable par son ID cote client
    private SparseArray<Set<Method>> clientPacketsHandlers;
    // Association des méthodes correspondant à un packet identifiable par son ID cote serveur
    private SparseArray<Set<Method>> serverPacketsHandlers;

    /**
     * Constructeur du gestionnaire
     */
    private PacketHandler() {
        this.clientPacketsHandlers = new SparseArray<>();
        this.serverPacketsHandlers = new SparseArray<>();
        SparseArray<Class<? extends Packet>> packetsByID = PacketManager.getInstance().getPacketsByID();
        Set<Class<? extends IHandler>> handlersClass = GameUtils.getAllSubClasses(IHandler.class, true);
        for (Class<? extends IHandler> handlerClass : handlersClass) {
            if (!handlerClass.isInterface() && !Modifier.isAbstract(handlerClass.getModifiers())) {
                Method[] classMethods = handlerClass.getMethods();
                for (Method classMethod : classMethods) {
                    if (Modifier.isStatic(classMethod.getModifiers()) && (classMethod.isAnnotationPresent(ClientHandler.class) || classMethod.isAnnotationPresent(ServerHandler.class))) {
                        Class<?>[] parametersType = classMethod.getParameterTypes();
                        if (((classMethod.isAnnotationPresent(ClientHandler.class) && parametersType.length == 1) || (classMethod.isAnnotationPresent(ServerHandler.class) && parametersType.length == 2 && ServerSenderReceiver.class.isAssignableFrom(parametersType[1]))) && Packet.class.isAssignableFrom(parametersType[0])) {
                            Class<? extends Packet> packetClass = (Class<? extends Packet>) parametersType[0];
                            int i = 0;
                            while (i < packetsByID.size() && !packetsByID.get(packetsByID.keyAt(i)).equals(packetClass)) {
                                ++i;
                            }
                            if (i != packetsByID.size()) {
                                int id = packetsByID.keyAt(i);
                                SparseArray<Set<Method>> packetsHandlers;
                                if (classMethod.isAnnotationPresent(ClientHandler.class)) {
                                    packetsHandlers = this.clientPacketsHandlers;
                                } else {
                                    packetsHandlers = this.serverPacketsHandlers;
                                }
                                Set<Method> packetHandlers = packetsHandlers.get(id);
                                if (packetHandlers != null) {
                                    packetHandlers.add(classMethod);
                                } else {
                                    packetHandlers = new HashSet<>();
                                    packetHandlers.add(classMethod);
                                    packetsHandlers.append(id, packetHandlers);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Méthode permettant de mettre en place le gestionnaire
     */
    public static synchronized void loadPacketsHandlers() {
        if (instance == null) {
            instance = new PacketHandler();
        }
    }

    /**
     * Méthode permettant de récupérer l'instance unique du gestionnaire
     *
     * @return L'instance unique du gestionnaire
     */
    public static PacketHandler getInstance() {
        if (instance == null) {
            instance = new PacketHandler();
        }
        return instance;
    }

    /**
     * Méthode de gestion d'un packet recu
     *
     * @param packet Le packet à gérer
     */
    public void handlePacket(Packet packet) {
        Set<Method> packetHandlers = this.clientPacketsHandlers.get(packet.getID());
        if (packetHandlers != null) {
            for (Method packetHandler : packetHandlers) {
                try {
                    packetHandler.invoke(null, packet);
                } catch (IllegalAccessException ignored) {
                } catch (InvocationTargetException ignored) {
                }
            }
        }
    }

    /**
     * Méthode de gestion d'un packet recu
     *
     * @param packet Le packet à gérer
     */
    public void handlePacket(Packet packet, ServerSenderReceiver serverSenderReceiver) {
        Set<Method> packetHandlers = this.serverPacketsHandlers.get(packet.getID());
        if (packetHandlers != null) {
            for (Method packetHandler : packetHandlers) {
                try {
                    packetHandler.invoke(null, packet, serverSenderReceiver);
                } catch (IllegalAccessException ignored) {
                } catch (InvocationTargetException ignored) {
                }
            }
        }
    }
}
