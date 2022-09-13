package io.thadow.parkourrun.utils.lib.titles;

import java.lang.reflect.Field;
import org.bukkit.entity.Player;
import org.bukkit.Bukkit;

public class Reflection {
    public static Class<?> getNMSClass(final String name) {
        final String version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
        return getClass("net.minecraft.server." + version + "." + name);
    }

    public static Class<?> getNMSClassArray(final String name) {
        final String version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
        return getClass("[Lnet.minecraft.server." + version + "." + name + ";");
    }

    public static Class<?> getCraftClass(final String name) {
        final String version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
        return getClass("org.bukkit.craftbukkit." + version + "." + name);
    }

    public static Class<?> getBukkitClass(final String name) {
        return getClass("org.bukkit." + name);
    }

    public static void sendPacket(final Player player, final Object packet) {
        try {
            final Object handle = player.getClass().getMethod("getHandle", (Class<?>[]) new Class[0]).invoke(player, new Object[0]);
            final Object playerConnection = handle.getClass().getField("playerConnection").get(handle);
            playerConnection.getClass().getMethod("sendPacket", getNMSClass("Packet")).invoke(playerConnection, packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static Object getFieldValue(final Object obj, final String name) {
        try {
            final Field field = obj.getClass().getDeclaredField(name);
            field.setAccessible(true);
            return field.get(obj);
        } catch (Exception exception) {
            return null;
        }
    }

    public static void setFieldValue(final Object obj, final String name, final Object value) {
        try {
            final Field field = obj.getClass().getDeclaredField(name);
            field.setAccessible(true);
            field.set(obj, value);
        } catch (Exception ex) {
        }
    }

    public static Class<?> getClass(final String name) {
        try {
            return Class.forName(name);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
}
