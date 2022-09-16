package io.thadow.parkourrun.arena;

import io.thadow.parkourrun.Main;
import io.thadow.parkourrun.arena.status.ArenaStatus;
import io.thadow.parkourrun.managers.CooldownManager;
import io.thadow.parkourrun.managers.PlayerDataManager;
import io.thadow.parkourrun.utils.configurations.ArenasConfiguration;
import io.thadow.parkourrun.utils.lib.scoreboard.ScoreboardType;
import io.thadow.parkourrun.utils.Utils;
import io.thadow.parkourrun.utils.configurations.MessagesConfiguration;
import io.thadow.parkourrun.utils.lib.titles.Titles;
import io.thadow.parkourrun.utils.storage.Storage;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Arena {
    private Player winner = null;
    private String winCorner1, winCorner2;
    private String arenaCorner1, arenaCorner2;
    private boolean enabled;
    private String arenaName;
    private String arenaDisplayName;
    private int maxTime, time, defTime, defMaxTime;
    private int minPlayers, maxPlayers;
    private Location spawn, waitLocation;
    private int reEnableCount;
    private ArenaStatus arenaStatus = ArenaStatus.WAITING;
    private ScoreboardType scoreboardType = ScoreboardType.WAITING;
    private final List<Player> players = new ArrayList<>();
    private int fireworksTaskID;
    private Map<Integer, String> checkpoints;
    private Map<Player, Integer> currentPlayerCheckpoint = new HashMap<>();
    private Map<Player, Integer> nextPlayerCheckpoint = new HashMap<>();

    public String getCheckpointCorners(Integer id) {
        return checkpoints.get(id);
    }

    public Map<Integer, String> getCheckpoints() {
        return checkpoints;
    }

    public Integer getPlayerCurrentCheckpoint(Player player) {
        return currentPlayerCheckpoint.get(player);
    }

    public void setCurrentPlayerCheckpoint(Player player, Integer id){
        currentPlayerCheckpoint.remove(player);
        currentPlayerCheckpoint.put(player, id);
    }

    public Integer getNextPlayerCheckpoint(Player player) {
        return nextPlayerCheckpoint.get(player);
    }

    public void setNextPlayerCheckpoint(Player player, Integer id) {
        nextPlayerCheckpoint.remove(player);
        nextPlayerCheckpoint.put(player, id);
    }

    public Arena(String arenaName, String arenaDisplayName, int minPlayers, int maxPlayers, Location spawn, Location waitLocation, int waiToStartTime, int waitToReEnableTime, int maxTime, String winCorner1, String winCorner2, String arenaCorner1, String arenaCorner2, Map<Integer, String> checkpoints, boolean enabled) {
        this.arenaName = arenaName;
        this.arenaDisplayName = arenaDisplayName;
        this.minPlayers = minPlayers;
        this.maxPlayers = maxPlayers;
        this.spawn = spawn;
        this.waitLocation = waitLocation;
        this.time = waiToStartTime;
        this.defTime = waiToStartTime;
        this.reEnableCount = waitToReEnableTime;
        this.maxTime = maxTime;
        this.defMaxTime = maxTime;
        this.winCorner1 = winCorner1;
        this.winCorner2 = winCorner2;
        this.arenaCorner1 = arenaCorner1;
        this.arenaCorner2 = arenaCorner2;
        this.checkpoints = checkpoints;
        this.enabled = enabled;
    }

    public String getWinCorner1() {
        return winCorner1;
    }

    public String getWinCorner2() {
        return winCorner2;
    }

    public void setWinCorner1(String corner1) {
        this.winCorner1 = corner1;
    }

    public void setWinCorner2(String corner2) {
        this.winCorner2 = corner2;
    }

    public String getArenaCorner1() {
        return arenaCorner1;
    }

    public void setArenaCorner1(String arenaCorner1) {
        this.arenaCorner1 = arenaCorner1;
    }

    public void setArenaCorner2(String arenaCorner2) {
        this.arenaCorner2 = arenaCorner2;
    }

    public String getArenaCorner2() {
        return arenaCorner2;
    }

    public void setArenaName(String arenaName) {
        this.arenaName = arenaName;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public int getDefTime() {
        return defTime;
    }

    public void setDefTime(int defTime) {
        this.defTime = defTime;
    }

    public int getDefMaxTime() {
        return defMaxTime;
    }

    public void setDefMaxTime(int defMaxTime) {
        this.defMaxTime = defMaxTime;
    }

    public void setReEnableCount(int reEnableCount) {
        this.reEnableCount = reEnableCount;
    }

    public void setMaxTime(int maxTime) {
        this.maxTime = maxTime;
    }

    public int getMaxTime() {
        return maxTime;
    }

    public void degreeMaxTime() {
        maxTime--;
    }

    public void addMaxTime() {
        maxTime--;
    }

    public void degreeTime() {
        time--;
    }

    public void addTime() {
        time++;
    }

    public String getArenaName() {
        return arenaName;
    }

    public String getArenaDisplayName() {
        return arenaDisplayName;
    }

    public void setArenaDisplayName(String arenaDisplayName) {
        this.arenaDisplayName = arenaDisplayName;
    }

    public void setMinPlayers(int minPlayers) {
        this.minPlayers = minPlayers;
    }

    public int getMinPlayers() {
        return minPlayers;
    }

    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public void setSpawn(Location spawn) {
        this.spawn = spawn;
    }

    public Location getSpawn() {
        return spawn;
    }

    public Location getWaitLocation() {
        return waitLocation;
    }

    public void setWaitLocation(Location waitLocation) {
        this.waitLocation = waitLocation;
    }

    public void setArenaStatus(ArenaStatus arenaStatus) {
        this.arenaStatus = arenaStatus;
    }

    public ArenaStatus getArenaStatus() {
        return arenaStatus;
    }

    public void setScoreboardType(ScoreboardType scoreboardType) {
        this.scoreboardType = scoreboardType;
    }

    public ScoreboardType getScoreboardType() {
        return scoreboardType;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public Player getWinner() {
        return winner;
    }

    public void setWinner(Player winner) {
        this.winner = winner;
    }

    public int getTime() {
        return time;
    }

    public int getReEnableCount() {
        return reEnableCount;
    }


    public void addPlayer(Player player) {
        if (arenaName == null || arenaName.isEmpty()) {
            String message = MessagesConfiguration.getPath("Messages.Invalid Arena Parameter.Arena ID");
            message = Utils.replace(message, "%arenaID%", arenaName);
            message = Utils.format(message);
            player.sendMessage(message);
            return;
        }
        if (arenaDisplayName == null || arenaDisplayName.isEmpty()) {
            String message = MessagesConfiguration.getPath("Messages.Invalid Arena Parameter.Arena Name");
            message = Utils.replace(message, "%arenaID%", arenaName);
            message = Utils.format(message);
            player.sendMessage(message);
            return;
        }
        if (getMinPlayers() == 0) {
            String message = MessagesConfiguration.getPath("Messages.Invalid Arena Parameter.Min Players");
            message = Utils.replace(message, "%arenaID%", arenaName);
            message = Utils.format(message);
            player.sendMessage(message);
            return;
        }
        if (getMaxPlayers() == 0) {
            String message = MessagesConfiguration.getPath("Messages.Invalid Arena Parameter.Max Players");
            message = Utils.replace(message, "%arenaID%", arenaName);
            message = Utils.format(message);
            player.sendMessage(message);
            return;
        }
        if (getMinPlayers() > getMaxPlayers()) {
            String message = MessagesConfiguration.getPath("Messages.Invalid Arena Parameter.Min > Max");
            message = Utils.replace(message, "%arenaID%", arenaName);
            message = Utils.format(message);
            player.sendMessage(message);
            return;
        }
        if (getSpawn() == null) {
            String message = MessagesConfiguration.getPath("Messages.Invalid Arena Parameter.Spawn Location");
            message = Utils.replace(message, "%arenaID%", arenaName);
            message = Utils.format(message);
            player.sendMessage(message);
            return;
        }
        if (getWaitLocation() == null) {
            String message = MessagesConfiguration.getPath("Messages.Invalid Arena Parameter.Wait Location");
            message = Utils.replace(message, "%arenaID%", arenaName);
            message = Utils.format(message);
            player.sendMessage(message);
            return;
        }
        if (getTime() == 0) {
            String message = MessagesConfiguration.getPath("Messages.Invalid Arena Parameter.Wait To Start");
            message = Utils.replace(message, "%arenaID%", arenaName);
            message = Utils.format(message);
            player.sendMessage(message);
            return;
        }
        if (getReEnableCount() == 0) {
            String message = MessagesConfiguration.getPath("Messages.Invalid Arena Parameter.Wait To Re-Enable");
            message = Utils.replace(message, "%arenaID%", arenaName);
            message = Utils.format(message);
            player.sendMessage(message);
            return;
        }
        if (winCorner1 == null || winCorner1.isEmpty()) {
            String message = MessagesConfiguration.getPath("Messages.Invalid Arena Parameter.Win Zone Corner");
            message = Utils.replace(message, "%arenaID%", arenaName);
            message = Utils.format(message);
            player.sendMessage(message);
            return;
        }
        if (winCorner2 == null || winCorner2.isEmpty()) {
            String message = MessagesConfiguration.getPath("Messages.Invalid Arena Parameter.Win Zone Corner");
            message = Utils.replace(message, "%arenaID%", arenaName);
            message = Utils.format(message);
            player.sendMessage(message);
            return;
        }
        if (arenaCorner1 == null || arenaCorner1.isEmpty()) {
            String message = MessagesConfiguration.getPath("Messages.Invalid Arena Parameter.Arena Zone Corner");
            message = Utils.replace(message, "%arenaID%", arenaName);
            message = Utils.format(message);
            player.sendMessage(message);
            return;
        }
        if (arenaCorner2 == null || arenaCorner2.isEmpty()) {
            String message = MessagesConfiguration.getPath("Messages.Invalid Arena Parameter.Arena Zone Corner");
            message = Utils.replace(message, "%arenaID%", arenaName);
            message = Utils.format(message);
            player.sendMessage(message);
            return;
        }
        if (getArenaStatus() == ArenaStatus.PLAYING) {
            String message = MessagesConfiguration.getPath("Messages.Arena.In Game");
            message = Utils.replace(message, "%arenaID%", arenaName);
            message = Utils.format(message);
            player.sendMessage(message);
            return;
        }
        if (getArenaStatus() == ArenaStatus.ENDING) {
            String message = MessagesConfiguration.getPath("Messages.Arena.Ending");
            message = Utils.replace(message, "%arenaID%", arenaName);
            message = Utils.format(message);
            player.sendMessage(message);
            return;
        }
        if (getPlayers().size() == getMaxPlayers()) {
            String message = MessagesConfiguration.getPath("Messages.Arena.Full");
            message = Utils.replace(message, "%arenaID%", arenaName);
            message = Utils.format(message);
            player.sendMessage(message);
            return;
        }
        if (!isEnabled()) {
            String message = MessagesConfiguration.getPath("Messages.Arena.Arena Disabled");
            message = Utils.format(message);
            player.sendMessage(message);
            return;
        }
        players.add(player);
        player.teleport(getWaitLocation());
        String message = MessagesConfiguration.getPath("Messages.Arena.Player Join");
        message = Utils.replace(message, "%player%", player.getName());
        message = Utils.replace(message, "%current%", String.valueOf(getPlayers().size()));
        message = Utils.replace(message, "%max%", String.valueOf(getMaxPlayers()));
        message = Utils.format(message);
        broadcast(message);
        checkArena();
    }

    public void removePlayer(Player player) {
        players.remove(player);
        if (getArenaStatus() == ArenaStatus.WAITING || getArenaStatus() == ArenaStatus.STARTING) {
            String message = MessagesConfiguration.getPath("Messages.Arena.Player Leave In Waiting");
            message = Utils.replace(message, "%player%", player.getName());
            message = Utils.replace(message, "%current%", String.valueOf(getPlayers().size()));
            message = Utils.replace(message, "%max%", String.valueOf(getMaxPlayers()));
            message = Utils.format(message);
            broadcast(message);
        }
        if (getArenaStatus() == ArenaStatus.PLAYING) {
            if (ArenasConfiguration.getConfiguration().getBoolean("Arenas." + arenaName + ".Extensions.Lose.Add Lose On Disconnect/Leave")) {
                Storage.getStorage().addLose(player);
            }
        }
        if (getArenaStatus() == ArenaStatus.PLAYING) {
            String message = MessagesConfiguration.getPath("Messages.Arena.Player Leave In Game");
            message = Utils.replace(message, "%player%", player.getName());
            message = Utils.replace(message, "%current%", String.valueOf(getPlayers().size()));
            message = Utils.replace(message, "%max%", String.valueOf(getMaxPlayers()));
            message = Utils.format(message);
            broadcast(message);
        }
        if (getPlayers().size() == 0 && getArenaStatus() == ArenaStatus.PLAYING) {
            finalizeArena(false);
            return;
        }
        if (getPlayers().size() < minPlayers && getArenaStatus() == ArenaStatus.STARTING) {
            setArenaStatus(ArenaStatus.WAITING);
            setTime(getDefTime());
            setMaxTime(getDefMaxTime());
            return;
        }
        if (getPlayers().size() == 1 && ArenasConfiguration.getConfiguration().getBoolean("Arenas." + arenaName + ".Extensions.Win.Last Player Wins")) {
            finalizeArenaWithWinner(getPlayers().get(0));
            return;
        }
        if (getArenaStatus() == ArenaStatus.WAITING || getArenaStatus() == ArenaStatus.STARTING) {
            checkArena();
        }
    }

    public void removePlayerSilent(Player player) {
        players.remove(player);
        if (getArenaStatus() == ArenaStatus.PLAYING) {
            if (ArenasConfiguration.getConfiguration().getBoolean("Arenas." + arenaName + ".Extensions.Lose.Add Lose On Disconnect/Leave")) {
                Storage.getStorage().addLose(player);
            }
        }
        if (getPlayers().size() == 0 && getArenaStatus() == ArenaStatus.PLAYING) {
            finalizeArena(false);
            return;
        }
        if (getPlayers().size() < minPlayers && getArenaStatus() == ArenaStatus.STARTING) {
            setArenaStatus(ArenaStatus.WAITING);
            setTime(getDefTime());
            setMaxTime(getDefMaxTime());
            return;
        }
        if (getPlayers().size() == 1 && ArenasConfiguration.getConfiguration().getBoolean("Arenas." + arenaName + ".Extensions.Win.Last Player Wins")) {
            finalizeArenaWithWinner(getPlayers().get(0));
            return;
        }
        if (getArenaStatus() == ArenaStatus.WAITING || getArenaStatus() == ArenaStatus.STARTING) {
            checkArena();
        }
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        if (!enabled) {
            arenaStatus = ArenaStatus.DISABLED;
            String message = MessagesConfiguration.getPath("Messages.Arena.Parameter Changed.Arena Disabled.Message To Players");
            message = Utils.format(message);
            broadcast(message);
            teleportSpawn(true);
        } else {
            arenaStatus = ArenaStatus.WAITING;
        }
    }

    public void broadcast(String message) {
        for (Player players : getPlayers()) {
            players.sendMessage(message);
        }
    }

    public void checkArena() {
        if (getArenaStatus() != ArenaStatus.STARTING) {
            if (players.size() >= minPlayers) {
                CooldownManager cooldown = new CooldownManager();
                cooldown.startGame(this);
                setArenaStatus(ArenaStatus.STARTING);
            }
        }
    }

    public void startArena() {
        setArenaStatus(ArenaStatus.PLAYING);
        CooldownManager cooldown = new CooldownManager();
        cooldown.startGameTime(this);
        for (Player players : getPlayers()) {
            setCurrentPlayerCheckpoint(players, 0);
            players.teleport(getSpawn());
        }
        List<String> messages = MessagesConfiguration.getListPath("Messages.Arena.Started.Message");
        for (String message : messages) {
            message = Utils.format(message);
            broadcast(message);
        }
        if (MessagesConfiguration.getBoolean("Messages.Arena.Started.Titles.Enabled")) {
            int fadeIn = MessagesConfiguration.getInt("Messages.Arena.Started.Titles.Fade In");
            int fadeOut = MessagesConfiguration.getInt("Messages.Arena.Started.Titles.Fade Out");
            int stay = MessagesConfiguration.getInt("Messages.Arena.Started.Titles.Stay");
            String title = MessagesConfiguration.getPath("Messages.Arena.Started.Titles.Title");
            String subTitle = MessagesConfiguration.getPath("Messages.Arena.Started.Titles.SubTitle");
            for (Player players : getPlayers()) {
                Titles.sendTitle(players, fadeIn, stay, fadeOut, Utils.colorize(title), Utils.colorize(subTitle));
            }
        }
        if (MessagesConfiguration.getBoolean("Messages.Arena.Started.Sound.Enabled")) {
            String soundPath = MessagesConfiguration.getPath("Messages.Arena.Started.Sound.Sound");
            for (Player players : getPlayers()) {
                Utils.playSound(players, soundPath);
            }
        }
    }

    public void finalizeArena(boolean closingServer) {
        if (closingServer) {
            teleportSpawn(true);
            return;
        }
        List<String> messages = MessagesConfiguration.getListPath("Messages.Arena.Tie.Message");
        for (String message : messages) {
                message = Utils.format(message);
                broadcast(message);
        }
        if (ArenasConfiguration.getConfiguration().getBoolean("Arenas." + arenaName + ".Extensions.Lose.Add Lose On Tie")) {
            for (Player players : getPlayers()) {
                Storage.getStorage().addLose(players);
            }
        }
        if (ArenasConfiguration.getConfiguration().getBoolean("Arenas." + arenaName + ".Extensions.Win.Add Win On Tie")) {
            for (Player players : getPlayers()) {
                Storage.getStorage().addWin(players);
            }
        }
        if (MessagesConfiguration.getBoolean("Messages.Arena.Tie.Titles.Enabled")) {
            int fadeIn = MessagesConfiguration.getInt("Messages.Arena.Tie.Titles.Fade In");
            int fadeOut = MessagesConfiguration.getInt("Messages.Arena.Tie.Titles.Fade Out");
            int stay = MessagesConfiguration.getInt("Messages.Arena.Tie.Titles.Stay");
            String title = MessagesConfiguration.getPath("Messages.Arena.Tie.Titles.Title");
            String subTitle = MessagesConfiguration.getPath("Messages.Arena.Tie.Titles.SubTitle");
            for (Player players : getPlayers()) {
                Titles.sendTitle(players, fadeIn, stay, fadeOut, Utils.colorize(title), Utils.colorize(subTitle));
            }
        }
        if (MessagesConfiguration.getBoolean("Messages.Arena.Tie.Sound.Enabled")) {
            String soundPath = MessagesConfiguration.getPath("Messages.Arena.Tie.Sound.Sound");
            for (Player players : getPlayers()) {
                Utils.playSound(players, soundPath);
            }
        }
        setArenaStatus(ArenaStatus.ENDING);
        Bukkit.getConsoleSender().sendMessage("[DEBUG] Arena " + arenaName + " ha sido finalizada");
        Bukkit.getConsoleSender().sendMessage("[DEBUG] ArenaStatus: " + arenaStatus.toString());
        Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> {
            setTime(getDefTime());
            setMaxTime(getDefMaxTime());
            teleportSpawn(false);
            Bukkit.getConsoleSender().sendMessage("[DEBUG] Arena " + arenaName +" ha sido habilitada nuevamente");
            Bukkit.getConsoleSender().sendMessage("[DEBUG] ArenaStatus: " + arenaStatus);
        }, 20L * getReEnableCount());
    }

    public void finalizeArenaWithWinner(Player winner) {
        setWinner(winner);
        Storage.getStorage().addWin(winner);
        for (Player losers : getPlayers()) {
            if (!(losers == winner)) {
                Storage.getStorage().addLose(losers);
            }
        }
        setArenaStatus(ArenaStatus.ENDING);
        List<String> messages = MessagesConfiguration.getListPath("Messages.Arena.Ended.Message");
        for (String message : messages) {
            message = Utils.replace(message, "%winner%", winner.getName());
            message = Utils.format(message);
            broadcast(message);
        }
        if (MessagesConfiguration.getBoolean("Messages.Arena.Ended.Titles.Winner.Enabled")) {
            int fadeIn = MessagesConfiguration.getInt("Messages.Arena.Ended.Titles.Winner.Fade In");
            int stay = MessagesConfiguration.getInt("Messages.Arena.Ended.Titles.Winner.Stay");
            int fadeOut = MessagesConfiguration.getInt("Messages.Arena.Ended.Titles.Winner.Fade Out");
            String title = MessagesConfiguration.getPath("Messages.Arena.Ended.Titles.Winner.Title");
            String subTitle = MessagesConfiguration.getPath("Messages.Arena.Ended.Titles.Winner.SubTitle");
            Titles.sendTitle(winner, fadeIn, stay, fadeOut, Utils.colorize(title), Utils.colorize(subTitle));
        }
        if (MessagesConfiguration.getBoolean("Messages.Arena.Ended.Titles.Losers.Enabled")) {
            int fadeIn = MessagesConfiguration.getInt("Messages.Arena.Ended.Titles.Losers.Fade In");
            int stay = MessagesConfiguration.getInt("Messages.Arena.Ended.Titles.Losers.Stay");
            int fadeOut = MessagesConfiguration.getInt("Messages.Arena.Ended.Titles.Losers.Fade Out");
            String title = MessagesConfiguration.getPath("Messages.Arena.Ended.Titles.Losers.Title");
            String subTitle = MessagesConfiguration.getPath("Messages.Arena.Ended.Titles.Losers.SubTitle");
            for (Player players : getPlayers()) {
                if (!players.getName().equalsIgnoreCase(getWinner().getName())) {
                    Titles.sendTitle(players, fadeIn, stay, fadeOut, Utils.colorize(title), Utils.colorize(subTitle));
                }
            }
        }
        if (MessagesConfiguration.getBoolean("Messages.Arena.Ended.Sound.Enabled")) {
            String soundPath = MessagesConfiguration.getPath("Messages.Arena.Ended.Sound.Sound");
            for (Player players : getPlayers()) {
                Utils.playSound(players, soundPath);
            }
        }
        if (MessagesConfiguration.getBoolean("Messages.Arena.Ended.Fireworks.Enabled")) {
            BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
            fireworksTaskID = scheduler.scheduleSyncRepeatingTask(Main.getInstance(), () -> {
                if (getArenaStatus() == ArenaStatus.ENDING  && getPlayers().contains(getWinner()) && getWinner() != null) {
                    fireworksForWinner(winner);
                } else {
                    cancel(this.fireworksTaskID);
                }
            }, 0L, 20L);
        }
        Bukkit.getConsoleSender().sendMessage("[DEBUG] Arena " + arenaName + " ha sido finalizada (With Winner)");
        Bukkit.getConsoleSender().sendMessage("[DEBUG] ArenaStatus: " + arenaStatus.toString());
        Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> {
            setWinner(null);
            setTime(getDefTime());
            setMaxTime(getDefMaxTime());
            teleportSpawn(false);
            Bukkit.getConsoleSender().sendMessage("[DEBUG] Arena " + arenaName +" ha sido habilitada nuevamente");
            Bukkit.getConsoleSender().sendMessage("[DEBUG] ArenaStatus: " + arenaStatus);
        }, 20L * getReEnableCount());
    }


    public void fireworksForWinner(Player player) {
        Firework firework = (Firework) player.getWorld().spawnEntity(player.getLocation(), EntityType.FIREWORK);
        FireworkMeta fireworkMeta = firework.getFireworkMeta();
        FireworkEffect.Type type = FireworkEffect.Type.BALL;
        Color color_1 = Color.AQUA;
        Color color_2 = Color.LIME;
        Color color_3 = Color.YELLOW;
        Color fade = Color.WHITE;

        FireworkEffect effect = FireworkEffect.builder().withColor(color_1, color_2, color_3).withFade(fade).with(type).build();
        fireworkMeta.addEffect(effect);
        fireworkMeta.setPower(1);
        firework.setFireworkMeta(fireworkMeta);
    }


    public void teleportSpawn(boolean disabling) {
        for (Player players : getPlayers()) {
            players.teleport(players.getWorld().getSpawnLocation());
        }
        if (disabling) {
            getPlayers().clear();
            return;
        }
        getPlayers().clear();
        setArenaStatus(ArenaStatus.WAITING);
    }

    public void cancel(int taskID) {
        Bukkit.getScheduler().cancelTask(taskID);
    }
}
