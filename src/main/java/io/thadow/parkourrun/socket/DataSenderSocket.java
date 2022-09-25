package io.thadow.parkourrun.socket;

import io.thadow.parkourrun.utils.Utils;
import org.bukkit.Bukkit;

import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class DataSenderSocket {
    public static List<String> lobbies = new ArrayList<>();
    public static final ConcurrentHashMap<String, Remote> sockets =  new ConcurrentHashMap<>();

    public static void sendMessage(String message) {
        for (String lobby : lobbies) {
            String[] split = lobby.split(":");

            if (sockets.containsKey(lobby)) {
                sockets.get(lobby).sendMessage(message);
            } else {
                try {
                    Socket socket = new Socket(split[0], Integer.parseInt(split[1]));
                    Remote remote = new Remote(socket, lobby);
                    if (remote.out != null) {
                        sockets.put(lobby, remote);
                        remote.sendMessage(message);
                    }
                } catch (Exception ignored) {
                }
            }
        }
    }

    public static void disable() {
        for (Remote rl : new ArrayList<>(sockets.values())) {
            String removeMessage = Utils.getRemoveMessage();
            rl.sendMessage(removeMessage);
            rl.disable();
        }
    }
}
