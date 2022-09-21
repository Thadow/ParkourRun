package io.thadow.parkourrun.arena;

import io.thadow.parkourrun.Main;
import io.thadow.parkourrun.api.event.*;
import io.thadow.parkourrun.arena.status.ArenaStatus;
import io.thadow.parkourrun.managers.CooldownManager;
import io.thadow.parkourrun.managers.SignsManager;
import io.thadow.parkourrun.utils.configurations.ArenaConfiguration;
import io.thadow.parkourrun.utils.Utils;
import io.thadow.parkourrun.utils.configurations.SignsConfiguration;
import io.thadow.parkourrun.utils.debug.Debugger;
import io.thadow.parkourrun.utils.debug.type.DebugType;
import io.thadow.parkourrun.utils.lib.titles.Titles;
import io.thadow.parkourrun.utils.storage.Storage;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.YamlConfiguration;
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
    private String arenaID;
    private String arenaDisplayName;
    private int maxTime, time, defTime, defMaxTime;
    private int minPlayers, maxPlayers;
    private Location spawn, waitLocation;
    private int reEnableTime, endingTime;
    private ArenaStatus arenaStatus = ArenaStatus.WAITING;
    private final List<Player> players = new ArrayList<>();
    private int fireworksTaskID;
    private Map<Integer, String> checkpoints;
    private final Map<Player, Integer> currentPlayerCheckpoint = new HashMap<>();
    private final Map<Player, Integer> nextPlayerCheckpoint = new HashMap<>();
    private final ArenaConfiguration configuration;
    private List<Block> signs = new ArrayList<>();

    public Arena(String arenaID) {
        this.arenaID = arenaID;
        Bukkit.getPluginManager().callEvent(new ArenaPreLoadEvent(this));

        configuration = new ArenaConfiguration(arenaID, Main.getInstance().getDataFolder() + "/Arenas");
        YamlConfiguration yamlConfiguration = configuration.getConfiguration();
        Bukkit.getPluginManager().callEvent(new ArenaConfigurationLoadEvent(this, yamlConfiguration));

        arenaDisplayName = yamlConfiguration.getString("Arena Name");
        minPlayers = yamlConfiguration.getInt("Min Players");
        maxPlayers = yamlConfiguration.getInt("Max Players");

        World spawnWorld;
        double spawnX, spawnY, spawnZ;
        float spawnYaw, spawnPitch;
        String spawnLocation = yamlConfiguration.getString("Spawn Location");
        String[] spawnLocationSplit = spawnLocation.split(";");
        spawnWorld = Bukkit.getWorld(spawnLocationSplit[0]);
        spawnX = Double.parseDouble(spawnLocationSplit[1]);
        spawnY = Double.parseDouble(spawnLocationSplit[2]);
        spawnZ = Double.parseDouble(spawnLocationSplit[3]);
        spawnYaw = Float.parseFloat(spawnLocationSplit[4]);
        spawnPitch = Float.parseFloat(spawnLocationSplit[5]);
        spawn = new Location(spawnWorld, spawnX, spawnY, spawnZ, spawnYaw, spawnPitch);

        World waitWorld;
        double waitX, waitY, waitZ;
        float waitYaw, waitPitch;
        String waitLocation = yamlConfiguration.getString("Wait Location");
        String[] waitLocationSplit = waitLocation.split(";");
        waitWorld = Bukkit.getWorld(waitLocationSplit[0]);
        waitX = Double.parseDouble(waitLocationSplit[1]);
        waitY = Double.parseDouble(waitLocationSplit[2]);
        waitZ = Double.parseDouble(waitLocationSplit[3]);
        waitYaw = Float.parseFloat(waitLocationSplit[4]);
        waitPitch = Float.parseFloat(waitLocationSplit[5]);
        this.waitLocation = new Location(waitWorld, waitX, waitY, waitZ, waitYaw, waitPitch);

        time = yamlConfiguration.getInt("Wait Time To Start");
        defTime = time;

        reEnableTime = yamlConfiguration.getInt("Re-Enable Time");

        endingTime = yamlConfiguration.getInt("Ending Time");

        maxTime = yamlConfiguration.getInt("Max Time");
        defMaxTime = maxTime;

        winCorner1 = yamlConfiguration.getString("Win Zone Corner 1");
        winCorner2 = yamlConfiguration.getString("Win Zone Corner 2");

        arenaCorner1 = yamlConfiguration.getString("Arena Zone Corner 1");
        arenaCorner2 = yamlConfiguration.getString("Arena Zone Corner 2");

        int totalCheckpoints = yamlConfiguration.getInt("Total Checkpoints");
        Map<Integer, String> checkpoints2 = new HashMap<>();
        if (yamlConfiguration.contains("Checkpoints.1")) {
            for (int checkpoint = 1; checkpoint <= totalCheckpoints; checkpoint++) {
                String location = "Checkpoints." + checkpoint + ".Location";
                String corner1 = "Checkpoints." + checkpoint + ".Corner 1";
                String corner2 = "Checkpoints." + checkpoint + ".Corner 2";
                String full = yamlConfiguration.getString(location) + "/-/" + yamlConfiguration.getString(corner1) + "/-/" + yamlConfiguration.getString(corner2);
                checkpoints2.put(checkpoint, full);
            }
        }
        this.checkpoints = checkpoints2;

        enabled = yamlConfiguration.getBoolean("Enabled");
        if (!enabled) {
            setArenaStatus(ArenaStatus.DISABLED);
        } else {
            setArenaStatus(ArenaStatus.WAITING);
        }

        registerSings();
        refreshSigns(this);
        Bukkit.getPluginManager().callEvent(new ArenaPostLoadEvent(this));
    }

    public void addSign(Location location) {
        if (location == null)
            return;
        if (location.getBlock().getType().toString().endsWith("_SIGN") || location.getBlock().getType().toString().endsWith("_WALL_SIGN")) {
            signs.add(location.getBlock());
            refreshSigns(this);
            SignsManager.updateBlock(this);
        }
    }

    public synchronized void refreshSigns(Arena arena) {
        for (Block signs2 : arena.getSigns()) {
            if (signs2 == null)
                return;
            if (!(signs2.getType().toString().endsWith("_SIGN") || signs2.getType().toString().endsWith("_WALL_SIGN")))
                return;
            if (!(signs2.getState() instanceof Sign))
                return;
            Sign sign = (Sign) signs2.getState();
            if (sign == null)
                return;
            int line = 0;
            for (String string : Main.getSignsConfiguration().getStringList("Format")) {
                if (string == null)
                    return;
                String waiting = Main.getInstance().getConfiguration().getString("Configuration.Arenas.Status.Waiting");
                String starting = Main.getInstance().getConfiguration().getString("Configuration.Arenas.Status.Starting");
                String playing = Main.getInstance().getConfiguration().getString("Configuration.Arenas.Status.Playing");
                String ending = Main.getInstance().getConfiguration().getString("Configuration.Arenas.Status.Ending");
                String disabled = Main.getInstance().getConfiguration().getString("Configuration.Arenas.Status.Disabled");
                String status;
                switch (arena.getArenaStatus()) {
                    case WAITING:
                        status = waiting;
                        break;
                    case STARTING:
                        status = starting;
                        break;
                    case PLAYING:
                        status = playing;
                        break;
                    case ENDING:
                        status = ending;
                        break;
                    case DISABLED:
                        status = disabled;
                        break;
                    default:
                        throw new IllegalStateException("Unexpected value: " + arena.getArenaStatus());
                }
                sign.setLine(line, Utils.colorize(string.replace("[current]", String.valueOf(arena.getPlayers().size()))
                        .replace("[max]", String.valueOf(arena.getMaxPlayers())).replace("[arena]", arena.getArenaDisplayName())
                        .replace("[status]", status)));
                line++;
            }
            try {
                sign.update(true);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public void registerSings() {
        for (String location : SignsConfiguration.signsConfiguration.getStringList("Locations")) {
            String[] locationSplit = location.split(";");
            if (locationSplit[0].equals(arenaDisplayName)) {
                double x = Double.parseDouble(locationSplit[1]);
                double y = Double.parseDouble(locationSplit[2]);
                double z = Double.parseDouble(locationSplit[3]);
                float yaw = Float.parseFloat(locationSplit[4]);
                float pitch = Float.parseFloat(locationSplit[5]);
                World world = Bukkit.getWorld(locationSplit[6]);
                Location signLocation = new Location(world, x, y, z, yaw, pitch);
                addSign(signLocation);
                Debugger.debug(DebugType.INFO, "Sign registered for: " + locationSplit[0]);
            }
        }
    }


    public void addPlayer(Player player) {
        if (arenaID == null || arenaID.isEmpty()) {
            String message = Main.getMessagesConfiguration().getString("Messages.Invalid Arena Parameter.Arena ID");
            message = Utils.replace(message, "%arenaID%", arenaID);
            message = Utils.format(message);
            player.sendMessage(message);
            return;
        }
        if (arenaDisplayName == null || arenaDisplayName.isEmpty()) {
            String message = Main.getMessagesConfiguration().getString("Messages.Invalid Arena Parameter.Arena Name");
            message = Utils.replace(message, "%arenaID%", arenaID);
            message = Utils.format(message);
            player.sendMessage(message);
            return;
        }
        if (getMinPlayers() == 0) {
            String message = Main.getMessagesConfiguration().getString("Messages.Invalid Arena Parameter.Min Players");
            message = Utils.replace(message, "%arenaID%", arenaID);
            message = Utils.format(message);
            player.sendMessage(message);
            return;
        }
        if (getMaxPlayers() == 0) {
            String message = Main.getMessagesConfiguration().getString("Messages.Invalid Arena Parameter.Max Players");
            message = Utils.replace(message, "%arenaID%", arenaID);
            message = Utils.format(message);
            player.sendMessage(message);
            return;
        }
        if (getMinPlayers() > getMaxPlayers()) {
            String message = Main.getMessagesConfiguration().getString("Messages.Invalid Arena Parameter.Min > Max");
            message = Utils.replace(message, "%arenaID%", arenaID);
            message = Utils.format(message);
            player.sendMessage(message);
            return;
        }
        if (getSpawn() == null) {
            String message = Main.getMessagesConfiguration().getString("Messages.Invalid Arena Parameter.Spawn Location");
            message = Utils.replace(message, "%arenaID%", arenaID);
            message = Utils.format(message);
            player.sendMessage(message);
            return;
        }
        if (getWaitLocation() == null) {
            String message = Main.getMessagesConfiguration().getString("Messages.Invalid Arena Parameter.Wait Location");
            message = Utils.replace(message, "%arenaID%", arenaID);
            message = Utils.format(message);
            player.sendMessage(message);
            return;
        }
        if (getTime() == 0) {
            String message = Main.getMessagesConfiguration().getString("Messages.Invalid Arena Parameter.Wait To Start");
            message = Utils.replace(message, "%arenaID%", arenaID);
            message = Utils.format(message);
            player.sendMessage(message);
            return;
        }
        if (getReEnableTime() == 0) {
            String message = Main.getMessagesConfiguration().getString("Messages.Invalid Arena Parameter.Re-Enable Time");
            message = Utils.replace(message, "%arenaID%", arenaID);
            message = Utils.format(message);
            player.sendMessage(message);
            return;
        }
        if (getEndingTime() == 0) {
            String message = Main.getMessagesConfiguration().getString("Messages.Invalid Arena Parameter.Ending Time");
            message = Utils.replace(message, "%arenaID%", arenaID);
            message = Utils.format(message);
            player.sendMessage(message);
            return;
        }
        if (getMaxTime() == 0) {
            String message = Main.getMessagesConfiguration().getString("Messages.Invalid Arena Parameter.Max Time");
            message = Utils.replace(message, "%arenaID%", arenaID);
            message = Utils.format(message);
            player.sendMessage(message);
            return;
        }
        if (winCorner1 == null || winCorner1.isEmpty()) {
            String message = Main.getMessagesConfiguration().getString("Messages.Invalid Arena Parameter.Win Zone Corner");
            message = Utils.replace(message, "%arenaID%", arenaID);
            message = Utils.format(message);
            player.sendMessage(message);
            return;
        }
        if (winCorner2 == null || winCorner2.isEmpty()) {
            String message = Main.getMessagesConfiguration().getString("Messages.Invalid Arena Parameter.Win Zone Corner");
            message = Utils.replace(message, "%arenaID%", arenaID);
            message = Utils.format(message);
            player.sendMessage(message);
            return;
        }
        if (arenaCorner1 == null || arenaCorner1.isEmpty()) {
            String message = Main.getMessagesConfiguration().getString("Messages.Invalid Arena Parameter.Arena Zone Corner");
            message = Utils.replace(message, "%arenaID%", arenaID);
            message = Utils.format(message);
            player.sendMessage(message);
            return;
        }
        if (arenaCorner2 == null || arenaCorner2.isEmpty()) {
            String message = Main.getMessagesConfiguration().getString("Messages.Invalid Arena Parameter.Arena Zone Corner");
            message = Utils.replace(message, "%arenaID%", arenaID);
            message = Utils.format(message);
            player.sendMessage(message);
            return;
        }
        if (getArenaStatus() == ArenaStatus.PLAYING) {
            String message = Main.getMessagesConfiguration().getString("Messages.Arena.In Game");
            message = Utils.replace(message, "%arenaID%", arenaID);
            message = Utils.format(message);
            player.sendMessage(message);
            return;
        }
        if (getArenaStatus() == ArenaStatus.ENDING) {
            String message = Main.getMessagesConfiguration().getString("Messages.Arena.Ending");
            message = Utils.replace(message, "%arenaID%", arenaID);
            message = Utils.format(message);
            player.sendMessage(message);
            return;
        }
        if (getPlayers().size() == getMaxPlayers()) {
            String message = Main.getMessagesConfiguration().getString("Messages.Arena.Full");
            message = Utils.replace(message, "%arenaID%", arenaID);
            message = Utils.format(message);
            player.sendMessage(message);
            return;
        }
        if (!isEnabled()) {
            String message = Main.getMessagesConfiguration().getString("Messages.Arena.Arena Disabled");
            message = Utils.format(message);
            player.sendMessage(message);
            return;
        }
        players.add(player);
        player.teleport(getWaitLocation());
        String message = Main.getMessagesConfiguration().getString("Messages.Arena.Player Join");
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
            String message = Main.getMessagesConfiguration().getString("Messages.Arena.Player Leave In Waiting");
            message = Utils.replace(message, "%player%", player.getName());
            message = Utils.replace(message, "%current%", String.valueOf(getPlayers().size()));
            message = Utils.replace(message, "%max%", String.valueOf(getMaxPlayers()));
            message = Utils.format(message);
            broadcast(message);
        }
        if (getArenaStatus() == ArenaStatus.PLAYING) {
            if (getConfiguration().getBoolean("Extensions.Lose.Add Lose On Disconnect/Leave")) {
                Storage.getStorage().addLose(player);
            }
        }
        if (getArenaStatus() == ArenaStatus.PLAYING) {
            String message = Main.getMessagesConfiguration().getString("Messages.Arena.Player Leave In Game");
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
        if (getPlayers().size() == 1 && getConfiguration().getBoolean("Extensions.Win.Last Player Win") && getArenaStatus() == ArenaStatus.PLAYING) {
            finalizeArenaWithWinner(getPlayers().get(0));
            return;
        }
        if (getPlayers().size() < minPlayers && getArenaStatus() == ArenaStatus.STARTING) {
            setArenaStatus(ArenaStatus.WAITING);
            return;
        }
        if (getArenaStatus() == ArenaStatus.WAITING) {
            checkArena();
        }
    }

    public void removePlayerSilent(Player player) {
        players.remove(player);
        if (getArenaStatus() == ArenaStatus.PLAYING) {
            if (getConfiguration().getBoolean("Extensions.Lose.Add Lose On Disconnect/Leave")) {
                Storage.getStorage().addLose(player);
            }
        }
        if (getPlayers().size() == 0 && getArenaStatus() == ArenaStatus.PLAYING) {
            finalizeArena(false);
            return;
        }
        if (getPlayers().size() == 1 && getConfiguration().getBoolean("Extensions.Win.Last Player Win") && getArenaStatus() == ArenaStatus.PLAYING) {
            finalizeArenaWithWinner(getPlayers().get(0));
            return;
        }
        if (getPlayers().size() < minPlayers && getArenaStatus() == ArenaStatus.STARTING) {
            setArenaStatus(ArenaStatus.WAITING);
            return;
        }
        if (getArenaStatus() == ArenaStatus.WAITING) {
            checkArena();
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
        List<String> messages = Main.getMessagesConfiguration().getStringList("Messages.Arena.Started.Message");
        for (String message : messages) {
            message = Utils.format(message);
            broadcast(message);
        }
        if (Main.getMessagesConfiguration().getBoolean("Messages.Arena.Started.Titles.Enabled")) {
            int fadeIn = Main.getMessagesConfiguration().getInt("Messages.Arena.Started.Titles.Fade In");
            int fadeOut = Main.getMessagesConfiguration().getInt("Messages.Arena.Started.Titles.Fade Out");
            int stay = Main.getMessagesConfiguration().getInt("Messages.Arena.Started.Titles.Stay");
            String title = Main.getMessagesConfiguration().getString("Messages.Arena.Started.Titles.Title");
            String subTitle = Main.getMessagesConfiguration().getString("Messages.Arena.Started.Titles.SubTitle");
            for (Player players : getPlayers()) {
                Titles.sendTitle(players, fadeIn, stay, fadeOut, Utils.colorize(title), Utils.colorize(subTitle));
            }
        }
        if (Main.getMessagesConfiguration().getBoolean("Messages.Arena.Started.Sound.Enabled")) {
            String soundPath = Main.getMessagesConfiguration().getString("Messages.Arena.Started.Sound.Sound");
            for (Player players : getPlayers()) {
                Utils.playSound(players, soundPath);
            }
        }
    }

    public void finalizeArena(boolean closingServer) {
        if (closingServer) {
            teleportLobby(true);
            return;
        }
        List<String> messages = Main.getMessagesConfiguration().getStringList("Messages.Arena.Tie.Message");
        for (String message : messages) {
            message = Utils.format(message);
            broadcast(message);
        }
        if (getConfiguration().getBoolean("Extensions.Lose.Add Lose On Tie")) {
            for (Player players : getPlayers()) {
                Storage.getStorage().addLose(players);
            }
        }
        if (getConfiguration().getBoolean("Extensions.Lose.Win.Add Win On Tie")) {
            for (Player players : getPlayers()) {
                Storage.getStorage().addWin(players);
            }
        }
        if (Main.getMessagesConfiguration().getBoolean("Messages.Arena.Tie.Titles.Enabled")) {
            int fadeIn = Main.getMessagesConfiguration().getInt("Messages.Arena.Tie.Titles.Fade In");
            int fadeOut = Main.getMessagesConfiguration().getInt("Messages.Arena.Tie.Titles.Fade Out");
            int stay = Main.getMessagesConfiguration().getInt("Messages.Arena.Tie.Titles.Stay");
            String title = Main.getMessagesConfiguration().getString("Messages.Arena.Tie.Titles.Title");
            String subTitle = Main.getMessagesConfiguration().getString("Messages.Arena.Tie.Titles.SubTitle");
            for (Player players : getPlayers()) {
                Titles.sendTitle(players, fadeIn, stay, fadeOut, Utils.colorize(title), Utils.colorize(subTitle));
            }
        }
        if (Main.getMessagesConfiguration().getBoolean("Messages.Arena.Tie.Sound.Enabled")) {
            String soundPath = Main.getMessagesConfiguration().getString("Messages.Arena.Tie.Sound.Sound");
            for (Player players : getPlayers()) {
                Utils.playSound(players, soundPath);
            }
        }
        setArenaStatus(ArenaStatus.ENDING);
        Bukkit.getConsoleSender().sendMessage("[DEBUG] Arena " + arenaID + " ha sido finalizada");
        Bukkit.getConsoleSender().sendMessage("[DEBUG] ArenaStatus: " + arenaStatus.toString());
        Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> {
            teleportLobby(false);
            Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> {
                setWinner(null);
                this.time = getDefTime();
                this.maxTime = getDefMaxTime();
                setArenaStatus(ArenaStatus.WAITING);
                Bukkit.getConsoleSender().sendMessage("[DEBUG] Arena " + arenaID +" ha sido habilitada nuevamente");
                Bukkit.getConsoleSender().sendMessage("[DEBUG] ArenaStatus: " + arenaStatus);
            }, 20L * getReEnableTime());
        }, 20L * getEndingTime());
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
        List<String> messages = Main.getMessagesConfiguration().getStringList("Messages.Arena.Ended.Message");
        for (String message : messages) {
            message = Utils.replace(message, "%winner%", winner.getName());
            message = Utils.format(message);
            broadcast(message);
        }
        if (Main.getMessagesConfiguration().getBoolean("Messages.Arena.Ended.Titles.Winner.Enabled")) {
            int fadeIn = Main.getMessagesConfiguration().getInt("Messages.Arena.Ended.Titles.Winner.Fade In");
            int stay = Main.getMessagesConfiguration().getInt("Messages.Arena.Ended.Titles.Winner.Stay");
            int fadeOut = Main.getMessagesConfiguration().getInt("Messages.Arena.Ended.Titles.Winner.Fade Out");
            String title = Main.getMessagesConfiguration().getString("Messages.Arena.Ended.Titles.Winner.Title");
            String subTitle = Main.getMessagesConfiguration().getString("Messages.Arena.Ended.Titles.Winner.SubTitle");
            Titles.sendTitle(winner, fadeIn, stay, fadeOut, Utils.colorize(title), Utils.colorize(subTitle));
        }
        if (Main.getMessagesConfiguration().getBoolean("Messages.Arena.Ended.Titles.Losers.Enabled")) {
            int fadeIn = Main.getMessagesConfiguration().getInt("Messages.Arena.Ended.Titles.Losers.Fade In");
            int stay = Main.getMessagesConfiguration().getInt("Messages.Arena.Ended.Titles.Losers.Stay");
            int fadeOut = Main.getMessagesConfiguration().getInt("Messages.Arena.Ended.Titles.Losers.Fade Out");
            String title = Main.getMessagesConfiguration().getString("Messages.Arena.Ended.Titles.Losers.Title");
            String subTitle = Main.getMessagesConfiguration().getString("Messages.Arena.Ended.Titles.Losers.SubTitle");
            for (Player players : getPlayers()) {
                if (!players.getName().equalsIgnoreCase(getWinner().getName())) {
                    Titles.sendTitle(players, fadeIn, stay, fadeOut, Utils.colorize(title), Utils.colorize(subTitle));
                }
            }
        }
        if (Main.getMessagesConfiguration().getBoolean("Messages.Arena.Ended.Sound.Enabled")) {
            String soundPath = Main.getMessagesConfiguration().getString("Messages.Arena.Ended.Sound.Sound");
            for (Player players : getPlayers()) {
                Utils.playSound(players, soundPath);
            }
        }
        if (Main.getMessagesConfiguration().getBoolean("Messages.Arena.Ended.Fireworks.Enabled")) {
            BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
            fireworksTaskID = scheduler.scheduleSyncRepeatingTask(Main.getInstance(), () -> {
                if (getArenaStatus() == ArenaStatus.ENDING  && getPlayers().contains(getWinner()) && getWinner() != null) {
                    fireworksForWinner(winner);
                } else {
                    cancel(this.fireworksTaskID);
                }
            }, 0L, 20L);
        }
        Bukkit.getConsoleSender().sendMessage("[DEBUG] Arena " + arenaID + " ha sido finalizada (With Winner)");
        Bukkit.getConsoleSender().sendMessage("[DEBUG] ArenaStatus: " + arenaStatus.toString());
        Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> {
            teleportLobby(false);
            Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> {
                setWinner(null);
                this.time = getDefTime();
                this.maxTime = getDefMaxTime();
                setArenaStatus(ArenaStatus.WAITING);
                Bukkit.getConsoleSender().sendMessage("[DEBUG] Arena " + arenaID +" ha sido habilitada nuevamente");
                Bukkit.getConsoleSender().sendMessage("[DEBUG] ArenaStatus: " + arenaStatus);
            }, 20L * getReEnableTime());
        }, 20L * getEndingTime());
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


    public void teleportLobby(boolean disabling) {
        if (disabling) {
            getPlayers().clear();
            return;
        }
        for (Player player : getPlayers()) {
            player.teleport(Utils.getLobbyLocation());
        }
        getPlayers().clear();
    }

    public void broadcast(String message) {
        for (Player players : getPlayers()) {
            players.sendMessage(message);
        }
    }

    public String getCheckpointCorners(Integer id) {
        return checkpoints.get(id);
    }

    public Map<Integer, String> getCheckpoints() {
        return checkpoints;
    }


    public void setCurrentPlayerCheckpoint(Player player, Integer id){
        currentPlayerCheckpoint.remove(player);
        currentPlayerCheckpoint.put(player, id);
    }

    public Integer getCurrentPlayerCheckpoint(Player player) {
        return currentPlayerCheckpoint.get(player);
    }

    public void setNextPlayerCheckpoint(Player player, Integer id) {
        nextPlayerCheckpoint.remove(player);
        nextPlayerCheckpoint.put(player, id);
    }

    public Integer getNextPlayerCheckpoint(Player player) {
        return nextPlayerCheckpoint.get(player);
    }

    public String getWinCorner1() {
        return winCorner1;
    }

    public String getWinCorner2() {
        return winCorner2;
    }

    public void setWinCorner1(String corner1) {
        this.winCorner1 = corner1;
        configuration.set("Win Zone Corner 1", corner1);
        configuration.save();
    }

    public void setWinCorner2(String corner2) {
        this.winCorner2 = corner2;
        configuration.set("Win Zone Corner 2", corner2);
        configuration.save();
    }

    public String getArenaCorner1() {
        return arenaCorner1;
    }

    public void setArenaCorner1(String arenaCorner1) {
        this.arenaCorner1 = arenaCorner1;
        configuration.set("Arena Zone Corner 1", arenaCorner1);
        configuration.save();
    }

    public void setArenaCorner2(String arenaCorner2) {
        this.arenaCorner2 = arenaCorner2;
        configuration.set("Arena Zone Corner 2", arenaCorner2);
        configuration.save();
    }

    public void addCheckpoint(int id, String location) {
        getCheckpoints().put(id, location);
        String[] locationSplit = location.split("/-/");
        int totalCheckpoints = configuration.getInt("Total Checkpoints");
        int newTotal = totalCheckpoints + 1;
        configuration.set("Checkpoints." + id + ".Corner 1", locationSplit[1]);
        configuration.set("Checkpoints." + id + ".Corner 2", locationSplit[2]);
        configuration.set("Checkpoints." + id + ".Location", locationSplit[0]);
        configuration.set("Total Checkpoints", newTotal);
        configuration.save();
    }

    public void deleteCheckpoint(int id) {
        getCheckpoints().remove(id);
        configuration.set("Checkpoints." + id + ".Location", null);
        configuration.set("Checkpoints." + id + ".Corner 2", null);
        configuration.set("Checkpoints." + id + ".Corner 1", null);
        configuration.set("Checkpoints." + id, null);
        configuration.set("Total Checkpoints", id - 1);
        configuration.save();
    }

    public YamlConfiguration getConfiguration() {
        return configuration.getConfiguration();
    }

    public String getArenaCorner2() {
        return arenaCorner2;
    }

    public void setArenaID(String arenaID) {
        this.arenaID = arenaID;
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

    public void setReEnableTime(int reEnableTime) {
        this.reEnableTime = reEnableTime;
        configuration.set("Re-Enable Time", reEnableTime);
        configuration.save();
    }

    public void setEndingTime(int endingTime) {
        this.endingTime = endingTime;
        configuration.set("Ending Time", endingTime);
        configuration.save();
    }

    public void setTime(int time, boolean save, boolean setting) {
        this.time = time;
        if (setting) {
            setDefTime(time);
        }
        if (save) {
            configuration.set("Wait Time To Start", time);
            configuration.save();
        }
    }

    public void setMaxTime(int maxTime, boolean save, boolean setting) {
        this.maxTime = maxTime;
        if (setting) {
            setDefMaxTime(maxTime);
        }
        if (save) {
            configuration.set("Max Time", maxTime);
            configuration.save();
        }
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

    public String getArenaID() {
        return arenaID;
    }

    public String getArenaDisplayName() {
        return arenaDisplayName;
    }

    public void setArenaDisplayName(String arenaDisplayName) {
        this.arenaDisplayName = arenaDisplayName;
        configuration.set("Arena Name", arenaDisplayName);
        configuration.save();
    }

    public void setMinPlayers(int minPlayers) {
        this.minPlayers = minPlayers;
        configuration.set("Min Players", minPlayers);
        configuration.save();
    }

    public int getMinPlayers() {
        return minPlayers;
    }

    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
        configuration.set("Max Players", maxPlayers);
        configuration.save();
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public void setSpawn(Location spawn, String locationString) {
        this.spawn = spawn;
        configuration.set("Spawn Location", locationString);
        configuration.save();
    }

    public Location getSpawn() {
        return spawn;
    }

    public Location getWaitLocation() {
        return waitLocation;
    }

    public void setWaitLocation(Location waitLocation, String waitLocationString) {
        this.waitLocation = waitLocation;
        configuration.set("Wait Location", waitLocationString);
        configuration.save();
    }

    public void setArenaStatus(ArenaStatus arenaStatus) {
        ArenaStatus oldStatus = this.arenaStatus;
        this.arenaStatus = arenaStatus;
        Bukkit.getPluginManager().callEvent(new ArenaChangeStatusEvent(this, oldStatus, arenaStatus));
    }

    public ArenaStatus getArenaStatus() {
        return arenaStatus;
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

    public int getReEnableTime() {
        return reEnableTime;
    }

    public int getEndingTime() {
        return endingTime;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        configuration.set("Enabled", enabled);
        configuration.save();
        if (!enabled) {
            teleportLobby(true);
            setArenaStatus(ArenaStatus.DISABLED);
            Bukkit.getPluginManager().callEvent(new ArenaDisableEvent(this));
            String message = Main.getMessagesConfiguration().getString("Messages.Arena.Parameter Changed.Arena Disabled.Message To Players");
            message = Utils.format(message);
            broadcast(message);
        } else {
            setArenaStatus(ArenaStatus.WAITING);
            Bukkit.getPluginManager().callEvent(new ArenaEnableEvent(this));
        }
    }

    public List<Block> getSigns() {
        return signs;
    }

    public void cancel(int taskID) {
        Bukkit.getScheduler().cancelTask(taskID);
    }
}
