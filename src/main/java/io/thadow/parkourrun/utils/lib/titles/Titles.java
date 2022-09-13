package io.thadow.parkourrun.utils.lib.titles;
import java.lang.reflect.Constructor;
import io.thadow.parkourrun.utils.Utils;
import org.bukkit.entity.Player;

public class Titles {
    public static void sendTitle(final Player player, final Integer fadeIn, final Integer stay, final Integer fadeOut, final String title, final String subtitle) {
        send(player, (fadeIn <= 0) ? 20 : fadeIn, (stay <= 0) ? 50 : stay, (fadeOut <= 0) ? 10 : fadeOut, Utils.colorize(title), Utils.colorize(subtitle));
    }

    private static void send(final Player player, final Integer fadeIn, final Integer stay, final Integer fadeOut, final String title, final String subtitle) {
        try {
            Object chat = Reflection.getNMSClass("IChatBaseComponent").getDeclaredClasses()[0].getMethod("a", String.class).invoke(null, "{\"text\":\"" + title + "\"}");
            Constructor<?> Constructor = Reflection.getNMSClass("PacketPlayOutTitle").getConstructor(Reflection.getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0], Reflection.getNMSClass("IChatBaseComponent"), Integer.TYPE, Integer.TYPE, Integer.TYPE);
            final Object timePacket = Constructor.newInstance(Reflection.getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0].getField("TIMES").get(null), chat, fadeIn, stay, fadeOut);
            Constructor = Reflection.getNMSClass("PacketPlayOutTitle").getConstructor(Reflection.getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0], Reflection.getNMSClass("IChatBaseComponent"));
            final Object titlePacket = Constructor.newInstance(Reflection.getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0].getField("TITLE").get(null), chat);
            chat = Reflection.getNMSClass("IChatBaseComponent").getDeclaredClasses()[0].getMethod("a", String.class).invoke(null, "{\"text\":\"" + subtitle + "\"}");
            final Object subtitlePacket = Constructor.newInstance(Reflection.getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0].getField("SUBTITLE").get(null), chat);
            Reflection.sendPacket(player, timePacket);
            Reflection.sendPacket(player, titlePacket);
            Reflection.sendPacket(player, subtitlePacket);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}
