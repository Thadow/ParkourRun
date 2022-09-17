package io.thadow.parkourrun.utils.storage;
import com.google.common.cache.CacheBuilder;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import org.bukkit.entity.Player;

public class ActionCooldown {
    private static Map<String, Map> cooldown = new ConcurrentHashMap<>();

    public static void createCooldown(String k, long defaultTime) {
        if (cooldown.containsKey(k))
            throw new IllegalArgumentException("Cooldown already exists.");
        cooldown.put(k, CacheBuilder.newBuilder().expireAfterWrite(defaultTime, TimeUnit.SECONDS).build().asMap());
    }

    public static Map getCooldownMap(String k) {
        if (cooldown.containsKey(k))
            return cooldown.get(k);
        return null;
    }

    public static void addCooldown(String k, Player p, int seconds) {
        if (!cooldown.containsKey(k))
            throw new IllegalArgumentException(k + " does not exist");
        long next = System.currentTimeMillis() + seconds * 1000L;
        ((Map<UUID, Long>)cooldown.get(k)).put(p.getUniqueId(), Long.valueOf(next));
    }

    public static boolean isOnCooldown(String k, Player p, long now) {
        return (cooldown.containsKey(k) && ((Map)cooldown.get(k)).containsKey(p.getUniqueId()) && now <= ((Long)((Map)cooldown.get(k)).get(p.getUniqueId())).longValue());
    }

    public static boolean isOnCooldown(String k, Player p) {
        return (cooldown.containsKey(k) && ((Map)cooldown.get(k)).containsKey(p.getUniqueId()) && System.currentTimeMillis() <= ((Long)((Map)cooldown.get(k)).get(p.getUniqueId())).longValue());
    }

    public static int getCooldownForPlayerInt(String k, Player p, long now) {
        return (int)(((Long)((Map)cooldown.get(k)).get(p.getUniqueId())).longValue() - now) / 1000;
    }

    public static int getCooldownForPlayerInt(String k, Player p) {
        return (int)(((Long)((Map)cooldown.get(k)).get(p.getUniqueId())).longValue() - System.currentTimeMillis()) / 1000;
    }

    public static long getCooldownForPlayerLong(String k, Player p) {
        return (int)(((Long)((Map)cooldown.get(k)).get(p.getUniqueId())).longValue() - System.currentTimeMillis());
    }

    public static long getCooldownForPlayerLong(String k, Player p, long now) {
        return ((Long)((Map)cooldown.get(k)).get(p.getUniqueId())).longValue() - now;
    }

    public static void removeCooldown(String k, Player p) {
        if (!cooldown.containsKey(k))
            throw new IllegalArgumentException(k + " does not exist");
        ((Map)cooldown.get(k)).remove(p.getUniqueId());
    }
}
