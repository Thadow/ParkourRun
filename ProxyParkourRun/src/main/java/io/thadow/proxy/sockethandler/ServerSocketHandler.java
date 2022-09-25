package io.thadow.proxy.sockethandler;

import io.thadow.proxy.Main;
import org.bukkit.Bukkit;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerSocketHandler {

    private static ServerSocketHandler instance = null;

    private ServerSocket serverSocket;
    public static boolean compute = true;
    private int task;

    public ServerSocketHandler(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        instance = this;
        compute = true;
        task = Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), ()-> {
            while (compute){
                try {
                    Socket s = serverSocket.accept();
                    Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), new ArenaSocket(s));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).getTaskId();
    }

    public static boolean init(int port){
        if (instance == null) {
            try {
                new ServerSocketHandler(port);
                return true;
            } catch (IOException e) {
                return false;
            }
        }
        return true;
    }

    public static void stopTasks(){
        if (instance != null){
            compute = false;
            Bukkit.getScheduler().cancelTask(instance.task);
        }
    }
}
