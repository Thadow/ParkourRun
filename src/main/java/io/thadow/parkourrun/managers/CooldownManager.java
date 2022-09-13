package io.thadow.parkourrun.managers;

import io.thadow.parkourrun.Main;
import io.thadow.parkourrun.arena.Arena;
import io.thadow.parkourrun.arena.status.ArenaStatus;
import io.thadow.parkourrun.utils.Utils;
import io.thadow.parkourrun.utils.configurations.MessagesConfiguration;
import io.thadow.parkourrun.utils.lib.titles.Titles;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;

public class CooldownManager {
    private int taskID;
    private int time;
    private Arena arena;

    public void startGame(Arena arena) {
        this.arena = arena;
        this.time = arena.getTime();
        if (arena.getArenaStatus() != ArenaStatus.STARTING) {
            String message = MessagesConfiguration.getPath("Messages.Arena.Starting In.Message");
            message = Utils.replace(message, "%seconds%", String.valueOf(arena.getTime()));
            message = Utils.format(message);
            arena.broadcast(message);
        }

        BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
        taskID = scheduler.scheduleSyncRepeatingTask(Main.getInstance(), () -> {
            if (!executeStartGame()) {
                arena.setTime(arena.getDefTime());
                cancel(this.taskID);
            }
        }, 0L, 20L);
    }

    protected boolean executeStartGame() {
        if (arena != null && arena.getArenaStatus() == ArenaStatus.STARTING) {
            int fadeIn = MessagesConfiguration.getInt("Messages.Arena.Starting In.Titles.Fade In");
            int fadeOut = MessagesConfiguration.getInt("Messages.Arena.Starting In.Titles.Fade Out");
            int stay = MessagesConfiguration.getInt("Messages.Arena.Starting In.Titles.Stay");
            String title = MessagesConfiguration.getPath("Messages.Arena.Starting In.Broadcast.Second " + time + ".Title");
            String subTitle = MessagesConfiguration.getPath("Messages.Arena.Starting In.Broadcast.Second " + time + ".SubTitle");
            if (MessagesConfiguration.getConfiguration().contains("Messages.Arena.Starting In.Broadcast.Second " + time)) {
                String message;
                if (time == 1) {
                    message = MessagesConfiguration.getPath("Messages.Arena.Starting In.Broadcast.Second " + time + ".Message");
                } else {
                    message = MessagesConfiguration.getPath("Messages.Arena.Starting In.Message");
                }
                message = Utils.replace(message, "%seconds%", String.valueOf(time));
                message = Utils.format(message);
                arena.broadcast(message);
                if (MessagesConfiguration.getBoolean("Messages.Arena.Starting In.Titles.Enabled")) {
                    for (Player players : arena.getPlayers()) {
                        Titles.sendTitle(players, fadeIn, stay, fadeOut, Utils.colorize(title), Utils.colorize(subTitle));
                    }
                }
                if (MessagesConfiguration.getBoolean("Messages.Arena.Starting In.Sound.Enabled")) {
                    String soundPath = MessagesConfiguration.getPath("Messages.Arena.Starting In.Sound.Sound");
                    for (Player players : arena.getPlayers()) {
                        Utils.playSound(players, soundPath);
                    }
                }
                arena.degreeTime();
                time--;
                return true;
            } else if (time <= 0) {
                arena.startArena();
                return false;
            } else {
                arena.degreeTime();
                time--;
                return true;
            }
        } else {
            String message = MessagesConfiguration.getPath("Messages.Arena.Countdown Stopped");
            message = Utils.format(message);
            arena.broadcast(message);
            return false;
        }
    }

    public void startGameTime(Arena arena) {
        this.arena = arena;
        this.time = arena.getMaxTime();
        arena.setMaxTime(time);

        BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
        taskID = scheduler.scheduleSyncRepeatingTask(Main.getInstance(), () -> {
            if (time == 0) {
                arena.finalizeArena(false);
                cancel(this.taskID);
                return;
            }
            if (!executeGameTime()) {
                cancel(this.taskID);
            }
        }, 0L, 20L);
    }

    protected boolean executeGameTime() {
        if (arena != null && arena.getArenaStatus() == ArenaStatus.PLAYING) {
            arena.degreeMaxTime();
            if (time == 0) {
                arena.finalizeArena(false);
                return false;
            } else {
                time--;
                return true;
            }
        } else {
            return false;
        }
    }


    public void cancel(int taskID) {
        Bukkit.getScheduler().cancelTask(taskID);
    }
}
