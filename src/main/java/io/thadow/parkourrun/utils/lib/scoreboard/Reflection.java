package io.thadow.parkourrun.utils.lib.scoreboard;

import java.util.HashMap;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.lang.reflect.Method;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

public class Reflection {
    private static final String OLD_PACKAGE_NAME;
    private static final Version VERSION;
    public static final Field PLAYER_SCORES;
    public static final Constructor<?> PACKET_SCORE_REMOVE;
    public static final Constructor<?> PACKET_SCORE;
    public static final Object ENUM_SCORE_ACTION_CHANGE;
    public static final Object ENUM_SCORE_ACTION_REMOVE;
    public static final Constructor<?> SB_SCORE;
    public static final Method SB_SCORE_SET;
    public static final Constructor<?> PACKET_OBJ;
    public static final Constructor<?> PACKET_DISPLAY;
    public static final Field PLAYER_CONNECTION;
    public static final Method SEND_PACKET;
    private static final Map<Class<?>, Method> HANDLES;

    public static Version getVersion() {
        return Reflection.VERSION;
    }

    public static Class<?> getClass(final String realPackageName, final String name) throws ClassNotFoundException {
        final String packageName = Reflection.VERSION.isBelow1_19() ? Reflection.OLD_PACKAGE_NAME : realPackageName;
        return Class.forName(packageName + "." + name);
    }

    public static Object getHandle(final Object obj) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        final Class<?> clazz = obj.getClass();
        if (!Reflection.HANDLES.containsKey(clazz)) {
            final Method method = clazz.getDeclaredMethod("getHandle", (Class<?>[]) new Class[0]);
            if (!method.isAccessible()) {
                method.setAccessible(true);
            }
            Reflection.HANDLES.put(clazz, method);
        }
        return Reflection.HANDLES.get(clazz).invoke(obj);
    }

    public static void sendPacket(final Object packet, final Player... players) {
        for (final Player p : players) {
            try {
                final Object playerConnection = Reflection.PLAYER_CONNECTION.get(getHandle(p));
                Reflection.SEND_PACKET.invoke(playerConnection, packet);
            } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    static {
        final String name = Bukkit.getServer().getClass().getPackage().getName();
        final String ver = name.substring(name.lastIndexOf(46) + 1);
        VERSION = new Version(ver);
        OLD_PACKAGE_NAME = "net.minecraft.server." + ver;
        Field playerScores = null;
        Constructor<?> packetScoreRemove = null;
        Constructor<?> packetScore = null;
        Object enumScoreActionChange = null;
        Object enumScoreActionRemove = null;
        Constructor<?> sbScore = null;
        Method sbScoreSet = null;
        Constructor<?> packetObj = null;
        Constructor<?> packetDisplay = null;
        Field playerConnection = null;
        Method sendPacket = null;
        try {
            final Class<?> packetScoreClass = getClass("net.minecraft.network.protocol.game", "PacketPlayOutScoreboardScore");
            final Class<?> packetDisplayClass = getClass("net.minecraft.network.protocol.game", "PacketPlayOutScoreboardDisplayObjective");
            final Class<?> packetObjClass = getClass("net.minecraft.network.protocol.game", "PacketPlayOutScoreboardObjective");
            final Class<?> scoreClass = getClass("net.minecraft.world.scores", "ScoreboardScore");
            final Class<?> sbClass = getClass("net.minecraft.world.scores", "Scoreboard");
            final Class<?> objClass = getClass("net.minecraft.world.scores", "ScoreboardObjective");
            final Class<?> playerClass = getClass("net.minecraft.server.level", "EntityPlayer");
            final Class<?> playerConnectionClass = getClass("net.minecraft.server.network", "PlayerConnection");
            final Class<?> packetClass = getClass("net.minecraft.network.protocol", "Packet");
            sbScore = scoreClass.getConstructor(sbClass, objClass, String.class);
            sbScoreSet = scoreClass.getMethod("setScore", Integer.TYPE);
            packetObj = packetObjClass.getConstructor(objClass, Integer.TYPE);
            packetDisplay = packetDisplayClass.getConstructor(Integer.TYPE, objClass);
            sendPacket = playerConnectionClass.getMethod("sendPacket", packetClass);
            if (Reflection.VERSION.isBelow1_19()) {
                playerScores = sbClass.getDeclaredField("playerScores");
                playerScores.setAccessible(true);
                playerConnection = playerClass.getField("playerConnection");
            } else {
                playerScores = sbClass.getDeclaredField("j");
                playerScores.setAccessible(true);
                playerConnection = playerClass.getField("b");
            }
            final String major = Reflection.VERSION.getMajor();
            switch (major) {
                case "1.7": {
                    packetScore = packetScoreClass.getConstructor(scoreClass, Integer.TYPE);
                    break;
                }
                case "1.8":
                case "1.9":
                case "1.10":
                case "1.11":
                case "1.12": {
                    packetScore = packetScoreClass.getConstructor(scoreClass);
                    packetScoreRemove = packetScoreClass.getConstructor(String.class, objClass);
                    break;
                }
                default: {
                    final Class<?> scoreActionClass = getClass("net.minecraft.server", "ScoreboardServer$Action");
                    packetScore = packetScoreClass.getConstructor(scoreActionClass, String.class, String.class, Integer.TYPE);
                    enumScoreActionChange = scoreActionClass.getEnumConstants()[0];
                    enumScoreActionRemove = scoreActionClass.getEnumConstants()[1];
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        PLAYER_SCORES = playerScores;
        PACKET_SCORE_REMOVE = packetScoreRemove;
        PACKET_SCORE = packetScore;
        ENUM_SCORE_ACTION_CHANGE = enumScoreActionChange;
        ENUM_SCORE_ACTION_REMOVE = enumScoreActionRemove;
        SB_SCORE = sbScore;
        SB_SCORE_SET = sbScoreSet;
        PACKET_OBJ = packetObj;
        PACKET_DISPLAY = packetDisplay;
        PLAYER_CONNECTION = playerConnection;
        SEND_PACKET = sendPacket;
        HANDLES = new HashMap<Class<?>, Method>();
    }

    public static class Version {
        private final String name;
        private final String major;
        private final String minor;

        Version(final String name) {
            this.name = name;
            final String[] splitName = name.split("_");
            this.major = splitName[0].substring(1) + "." + splitName[1];
            this.minor = splitName[2].substring(1);
        }

        public String getName() {
            return this.name;
        }

        public String getMajor() {
            return this.major;
        }

        public String getMinor() {
            return this.minor;
        }

        public boolean isBelow1_19() {
            return this.major.equals("1.7") || this.major.equals("1.8") || this.major.equals("1.9") || this.major.equals("1.10") || this.major.equals("1.11") || this.major.equals("1.12") || this.major.equals("1.13") || this.major.equals("1.14") || this.major.equals("1.15") || this.major.equals("1.16") || this.major.equals("1.17") || this.major.equals("1.18");
        }
    }
}
