package io.thadow.proxy.sockethandler;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import io.thadow.parkourrun.utils.Utils;
import io.thadow.proxy.debug.Debugger;
import io.thadow.proxy.debug.type.DebugType;
import org.bukkit.Bukkit;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class ArenaSocket implements Runnable {
    private final Socket socket;

    private Scanner scanner;

    private PrintWriter out;

    public ArenaSocket(Socket socket) {
        this.socket = socket;
        try {
            this.out = new PrintWriter(socket.getOutputStream(), true);
            this.scanner = new Scanner(socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        Debugger.debug(DebugType.INFO, "&aRemote Socket connected from: " + socket.getInetAddress());
    }

    @Override
    public void run() {
        while (ServerSocketHandler.compute && socket.isConnected()) {
            JsonObject jsonObject;
            if (!scanner.hasNext()) {
                return;
            }

            String message = scanner.next();

            try {
                JsonElement jsonElement = (new JsonParser()).parse(message);
                if (jsonElement.isJsonNull() || !jsonElement.isJsonObject()) {
                    Debugger.debug(DebugType.WARN, "&7Bad data received from: " + socket.getInetAddress());
                    return;
                }
                jsonObject = jsonElement.getAsJsonObject();
            } catch (JsonSyntaxException exception) {
                Debugger.debug(DebugType.WARN, "&7Bad data received from: " + socket.getInetAddress());
                return;
            }

            if (!jsonObject.has("Type")) {
                return;
            }


            if (jsonObject.get("Type").getAsString().equals("UPDATE")) {
                if (!jsonObject.has("serverID")) {
                    return;
                }
                if (!jsonObject.has("arenaID")) {
                    return;
                }
                if (!jsonObject.has("arenaName")) {
                    return;
                }
                if (!jsonObject.has("status")) {
                    return;
                }
                if (!jsonObject.has("currentPlayers")) {
                    return;
                }
                if (!jsonObject.has("maxPlayers")) {
                    return;
                }

                String serverID = jsonObject.get("serverID").getAsString();
                String arenaID = jsonObject.get("arenaID").getAsString();
                String arenaName = jsonObject.get("arenaName").getAsString();
                String status = jsonObject.get("status").getAsString();
                int currentPlayers = jsonObject.get("currentPlayers").getAsInt();
                int maxPlayers = jsonObject.get("maxPlayers").getAsInt();
                // Format -> serverID/-/arenaID/-/arenaName/-/status/-/currentPlayers/-/maxPlayers
                String format = serverID
                        + "/-/" + arenaID
                        + "/-/" + arenaName
                        + "/-/" + status
                        + "/-/" + currentPlayers
                        + "/-/" + maxPlayers;
                Utils.updateBungeeArena(format, socket.getInetAddress().toString());
            } else if (jsonObject.get("Type").getAsString().equals("REMOVE")) {
                if (!jsonObject.has("serverID")) {
                    return;
                }
                String serverID = jsonObject.get("serverID").getAsString();
                Utils.removeBungeeArena(serverID, socket.getInetAddress().toString());
            }
        }
    }
}
