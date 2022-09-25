package io.thadow.parkourrun.socket;

import io.thadow.parkourrun.utils.Utils;
import io.thadow.parkourrun.utils.debug.Debugger;
import io.thadow.parkourrun.utils.debug.type.DebugType;
import org.bukkit.Bukkit;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Remote {
    private final Socket socket;
    public PrintWriter out;
    public Scanner in;
    private final String remote;

    public Remote(Socket socket, String remote) {
        this.socket = socket;
        this.remote = remote;

        try {
            out = new PrintWriter(socket.getOutputStream());
        } catch (IOException exception) {
            out = null;
            return;
        }

        try {
            in = new Scanner(socket.getInputStream());
        } catch (IOException exception) {
            return;
        }

        Debugger.debug(DebugType.INFO, "&aRemote Socket created for: " + remote + " - " + socket.getInetAddress());
    }

    @SuppressWarnings("UnusedReturnValue")
    public boolean sendMessage(String message) {
        if (socket == null) {
            disable();
            return false;
        }
        if (!socket.isConnected()) {
            disable();
            return false;
        }
        if (out == null) {
            disable();
            return false;
        }
        if (in == null) {
            disable();
            return false;
        }
        if (out.checkError()) {
            disable();
            return false;
        }
        out.println(message);
        return true;
    }

    public void disable() {
        Debugger.debug(DebugType.INFO, "&cDisabling Remote Socket: " + remote + " - " + socket.getInetAddress());
        String removeMessage = Utils.getRemoveMessage();
        DataSenderSocket.sendMessage(removeMessage);
        DataSenderSocket.sockets.remove(remote);
        try {
            socket.close();
        } catch (IOException exception) {
            exception.printStackTrace();
        }

        in = null;
        out = null;
    }
}
