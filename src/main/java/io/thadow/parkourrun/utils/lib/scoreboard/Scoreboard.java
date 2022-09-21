package io.thadow.parkourrun.utils.lib.scoreboard;

import io.thadow.parkourrun.Main;
import io.thadow.parkourrun.api.ParkourRunAPI;
import io.thadow.parkourrun.arena.Arena;
import io.thadow.parkourrun.arena.status.ArenaStatus;
import io.thadow.parkourrun.managers.ArenaManager;
import io.thadow.parkourrun.utils.Utils;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class Scoreboard {
    public static final Map<UUID, Scoreboard> scoreboards = new HashMap<>();

    private static final Map<Class<?>, Field[]> PACKETS = new HashMap<>(8);
    private static final String[] COLOR_CODES = Arrays.stream(ChatColor.values())
            .map(Object::toString)
            .toArray(String[]::new);
    private static final VersionType VERSION_TYPE;
    // Packets and components
    private static final Class<?> CHAT_COMPONENT_CLASS;
    private static final Class<?> CHAT_FORMAT_ENUM;
    private static final Object EMPTY_MESSAGE;
    private static final Object RESET_FORMATTING;
    private static final MethodHandle MESSAGE_FROM_STRING;
    private static final MethodHandle PLAYER_CONNECTION;
    private static final MethodHandle SEND_PACKET;
    private static final MethodHandle PLAYER_GET_HANDLE;
    // Scoreboard packets
    private static final Reflection.PacketConstructor PACKET_SB_OBJ;
    private static final Reflection.PacketConstructor PACKET_SB_DISPLAY_OBJ;
    private static final Reflection.PacketConstructor PACKET_SB_SCORE;
    private static final Reflection.PacketConstructor PACKET_SB_TEAM;
    private static final Reflection.PacketConstructor PACKET_SB_SERIALIZABLE_TEAM;
    // Scoreboard enums
    private static final Class<?> ENUM_SB_HEALTH_DISPLAY;
    private static final Class<?> ENUM_SB_ACTION;
    private static final Object ENUM_SB_HEALTH_DISPLAY_INTEGER;
    private static final Object ENUM_SB_ACTION_CHANGE;
    private static final Object ENUM_SB_ACTION_REMOVE;

    static {
        try {
            MethodHandles.Lookup lookup = MethodHandles.lookup();

            if (Reflection.isRepackaged()) {
                VERSION_TYPE = VersionType.V1_17;
            } else if (Reflection.nmsOptionalClass(null, "ScoreboardServer$Action").isPresent()) {
                VERSION_TYPE = VersionType.V1_13;
            } else if (Reflection.nmsOptionalClass(null, "IScoreboardCriteria$EnumScoreboardHealthDisplay").isPresent()) {
                VERSION_TYPE = VersionType.V1_8;
            } else {
                VERSION_TYPE = VersionType.V1_7;
            }

            String gameProtocolPackage = "network.protocol.game";
            Class<?> craftPlayerClass = Reflection.obcClass("entity.CraftPlayer");
            Class<?> craftChatMessageClass = Reflection.obcClass("util.CraftChatMessage");
            Class<?> entityPlayerClass = Reflection.nmsClass("server.level", "EntityPlayer");
            Class<?> playerConnectionClass = Reflection.nmsClass("server.network", "PlayerConnection");
            Class<?> packetClass = Reflection.nmsClass("network.protocol", "Packet");
            Class<?> packetSbObjClass = Reflection.nmsClass(gameProtocolPackage, "PacketPlayOutScoreboardObjective");
            Class<?> packetSbDisplayObjClass = Reflection.nmsClass(gameProtocolPackage, "PacketPlayOutScoreboardDisplayObjective");
            Class<?> packetSbScoreClass = Reflection.nmsClass(gameProtocolPackage, "PacketPlayOutScoreboardScore");
            Class<?> packetSbTeamClass = Reflection.nmsClass(gameProtocolPackage, "PacketPlayOutScoreboardTeam");
            Class<?> sbTeamClass = VersionType.V1_17.isHigherOrEqual()
                    ? Reflection.innerClass(packetSbTeamClass, innerClass -> !innerClass.isEnum()) : null;
            Field playerConnectionField = Arrays.stream(entityPlayerClass.getFields())
                    .filter(field -> field.getType().isAssignableFrom(playerConnectionClass))
                    .findFirst().orElseThrow(NoSuchFieldException::new);
            Method sendPacketMethod = Arrays.stream(playerConnectionClass.getMethods())
                    .filter(m -> m.getParameterCount() == 1 && m.getParameterTypes()[0] == packetClass)
                    .findFirst().orElseThrow(NoSuchMethodException::new);

            MESSAGE_FROM_STRING = lookup.unreflect(craftChatMessageClass.getMethod("fromString", String.class));
            CHAT_COMPONENT_CLASS = Reflection.nmsClass("network.chat", "IChatBaseComponent");
            CHAT_FORMAT_ENUM = Reflection.nmsClass(null, "EnumChatFormat");
            EMPTY_MESSAGE = Array.get(MESSAGE_FROM_STRING.invoke(""), 0);
            RESET_FORMATTING = Reflection.enumValueOf(CHAT_FORMAT_ENUM, "RESET", 21);
            PLAYER_GET_HANDLE = lookup.findVirtual(craftPlayerClass, "getHandle", MethodType.methodType(entityPlayerClass));
            PLAYER_CONNECTION = lookup.unreflectGetter(playerConnectionField);
            SEND_PACKET = lookup.unreflect(sendPacketMethod);
            PACKET_SB_OBJ = Reflection.findPacketConstructor(packetSbObjClass, lookup);
            PACKET_SB_DISPLAY_OBJ = Reflection.findPacketConstructor(packetSbDisplayObjClass, lookup);
            PACKET_SB_SCORE = Reflection.findPacketConstructor(packetSbScoreClass, lookup);
            PACKET_SB_TEAM = Reflection.findPacketConstructor(packetSbTeamClass, lookup);
            PACKET_SB_SERIALIZABLE_TEAM = sbTeamClass == null ? null : Reflection.findPacketConstructor(sbTeamClass, lookup);

            for (Class<?> clazz : Arrays.asList(packetSbObjClass, packetSbDisplayObjClass, packetSbScoreClass, packetSbTeamClass, sbTeamClass)) {
                if (clazz == null) {
                    continue;
                }
                Field[] fields = Arrays.stream(clazz.getDeclaredFields())
                        .filter(field -> !Modifier.isStatic(field.getModifiers()))
                        .toArray(Field[]::new);
                for (Field field : fields) {
                    field.setAccessible(true);
                }
                PACKETS.put(clazz, fields);
            }

            if (VersionType.V1_8.isHigherOrEqual()) {
                String enumSbActionClass = VersionType.V1_13.isHigherOrEqual()
                        ? "ScoreboardServer$Action"
                        : "PacketPlayOutScoreboardScore$EnumScoreboardAction";
                ENUM_SB_HEALTH_DISPLAY = Reflection.nmsClass("world.scores.criteria", "IScoreboardCriteria$EnumScoreboardHealthDisplay");
                ENUM_SB_ACTION = Reflection.nmsClass("server", enumSbActionClass);
                ENUM_SB_HEALTH_DISPLAY_INTEGER = Reflection.enumValueOf(ENUM_SB_HEALTH_DISPLAY, "INTEGER", 0);
                ENUM_SB_ACTION_CHANGE = Reflection.enumValueOf(ENUM_SB_ACTION, "CHANGE", 0);
                ENUM_SB_ACTION_REMOVE = Reflection.enumValueOf(ENUM_SB_ACTION, "REMOVE", 1);
            } else {
                ENUM_SB_HEALTH_DISPLAY = null;
                ENUM_SB_ACTION = null;
                ENUM_SB_HEALTH_DISPLAY_INTEGER = null;
                ENUM_SB_ACTION_CHANGE = null;
                ENUM_SB_ACTION_REMOVE = null;
            }
        } catch (Throwable t) {
            throw new ExceptionInInitializerError(t);
        }
    }

    private final Player player;
    private final String id;

    private final List<String> lines = new ArrayList<>();
    private String title = ChatColor.RESET.toString();

    private boolean deleted = false;

    public Scoreboard(Player player) {
        this.player = Objects.requireNonNull(player, "player");
        this.id = "fb-" + Integer.toHexString(ThreadLocalRandom.current().nextInt());

        try {
            sendObjectivePacket(ObjectiveMode.CREATE);
            sendDisplayObjectivePacket();
        } catch (Throwable t) {
            throw new RuntimeException("Unable to create scoreboard", t);
        }
    }

    /**
     * Get the scoreboard title.
     *
     * @return the scoreboard title
     */
    public String getTitle() {
        return this.title;
    }

    /**
     * Update the scoreboard title.
     *
     * @param title the new scoreboard title
     * @throws IllegalArgumentException if the title is longer than 32 chars on 1.12 or lower
     * @throws IllegalStateException    if {@link #delete()} was call before
     */
    public void updateTitle(String title) {
        if (this.title.equals(Objects.requireNonNull(title, "title"))) {
            return;
        }

        if (!VersionType.V1_13.isHigherOrEqual() && title.length() > 32) {
            throw new IllegalArgumentException("Title is longer than 32 chars");
        }

        this.title = title;

        try {
            sendObjectivePacket(ObjectiveMode.UPDATE);
        } catch (Throwable t) {
            throw new RuntimeException("Unable to update scoreboard title", t);
        }
    }

    /**
     * Get the scoreboard lines.
     *
     * @return the scoreboard lines
     */
    public List<String> getLines() {
        return new ArrayList<>(this.lines);
    }

    /**
     * Get the specified scoreboard line.
     *
     * @param line the line number
     * @return the line
     * @throws IndexOutOfBoundsException if the line is higher than {@code size}
     */
    public String getLine(int line) {
        checkLineNumber(line, true, false);

        return this.lines.get(line);
    }

    /**
     * Update a single scoreboard line.
     *
     * @param line the line number
     * @param text the new line text
     * @throws IndexOutOfBoundsException if the line is higher than {@link #size() size() + 1}
     */
    public synchronized void updateLine(int line, String text) {
        checkLineNumber(line, false, true);

        try {
            if (line < size()) {
                this.lines.set(line, text);

                sendTeamPacket(getScoreByLine(line), TeamMode.UPDATE);
                return;
            }

            List<String> newLines = new ArrayList<>(this.lines);

            if (line > size()) {
                for (int i = size(); i < line; i++) {
                    newLines.add("");
                }
            }

            newLines.add(text);

            updateLines(newLines);
        } catch (Throwable t) {
            throw new RuntimeException("Unable to update scoreboard lines", t);
        }
    }

    /**
     * Remove a scoreboard line.
     *
     * @param line the line number
     */
    public synchronized void removeLine(int line) {
        checkLineNumber(line, false, false);

        if (line >= size()) {
            return;
        }

        List<String> newLines = new ArrayList<>(this.lines);
        newLines.remove(line);
        updateLines(newLines);
    }

    /**
     * Update all the scoreboard lines.
     *
     * @param lines the new lines
     * @throws IllegalArgumentException if one line is longer than 30 chars on 1.12 or lower
     * @throws IllegalStateException    if {@link #delete()} was call before
     */
    public void updateLines(String... lines) {
        updateLines(Arrays.asList(lines));
    }

    /**
     * Update the lines of the scoreboard
     *
     * @param lines the new scoreboard lines
     * @throws IllegalArgumentException if one line is longer than 30 chars on 1.12 or lower
     * @throws IllegalStateException    if {@link #delete()} was call before
     */
    public synchronized void updateLines(Collection<String> lines) {
        Objects.requireNonNull(lines, "lines");
        checkLineNumber(lines.size(), false, true);

        if (!VersionType.V1_13.isHigherOrEqual()) {
            int lineCount = 0;
            for (String s : lines) {
                if (s != null && s.length() > 30) {
                    throw new IllegalArgumentException("Line " + lineCount + " is longer than 30 chars");
                }
                lineCount++;
            }
        }

        List<String> oldLines = new ArrayList<>(this.lines);
        this.lines.clear();
        this.lines.addAll(lines);

        int linesSize = this.lines.size();

        try {
            if (oldLines.size() != linesSize) {
                List<String> oldLinesCopy = new ArrayList<>(oldLines);

                if (oldLines.size() > linesSize) {
                    for (int i = oldLinesCopy.size(); i > linesSize; i--) {
                        sendTeamPacket(i - 1, TeamMode.REMOVE);
                        sendScorePacket(i - 1, ScoreboardAction.REMOVE);

                        oldLines.remove(0);
                    }
                } else {
                    for (int i = oldLinesCopy.size(); i < linesSize; i++) {
                        sendScorePacket(i, ScoreboardAction.CHANGE);
                        sendTeamPacket(i, TeamMode.CREATE);

                        oldLines.add(oldLines.size() - i, getLineByScore(i));
                    }
                }
            }

            for (int i = 0; i < linesSize; i++) {
                if (!Objects.equals(getLineByScore(oldLines, i), getLineByScore(i))) {
                    sendTeamPacket(i, TeamMode.UPDATE);
                }
            }
        } catch (Throwable t) {
            throw new RuntimeException("Unable to update scoreboard lines", t);
        }
    }

    /**
     * Get the player who has the scoreboard.
     *
     * @return current player for this FastBoard
     */
    public Player getPlayer() {
        return this.player;
    }

    /**
     * Get the scoreboard id.
     *
     * @return the id
     */
    public String getId() {
        return this.id;
    }

    /**
     * Get if the scoreboard is deleted.
     *
     * @return true if the scoreboard is deleted
     */
    public boolean isDeleted() {
        return this.deleted;
    }

    /**
     * Get the scoreboard size (the number of lines).
     *
     * @return the size
     */
    public int size() {
        return this.lines.size();
    }

    /**
     * Delete this FastBoard, and will remove the scoreboard for the associated player if he is online.
     * After this, all uses of {@link #updateLines} and {@link #updateTitle} will throws an {@link IllegalStateException}
     *
     * @throws IllegalStateException if this was already call before
     */
    public void delete() {
        try {
            for (int i = 0; i < this.lines.size(); i++) {
                sendTeamPacket(i, TeamMode.REMOVE);
            }

            sendObjectivePacket(ObjectiveMode.REMOVE);
        } catch (Throwable t) {
            throw new RuntimeException("Unable to delete scoreboard", t);
        }

        this.deleted = true;
    }

    /**
     * Return if the player has a prefix/suffix characters limit.
     * By default, it returns true only in 1.12 or lower.
     * This method can be overridden to fix compatibility with some versions support plugin.
     *
     * @return max length
     */
    protected boolean hasLinesMaxLength() {
        return !VersionType.V1_13.isHigherOrEqual();
    }

    private void checkLineNumber(int line, boolean checkInRange, boolean checkMax) {
        if (line < 0) {
            throw new IllegalArgumentException("Line number must be positive");
        }

        if (checkInRange && line >= this.lines.size()) {
            throw new IllegalArgumentException("Line number must be under " + this.lines.size());
        }

        if (checkMax && line >= COLOR_CODES.length - 1) {
            throw new IllegalArgumentException("Line number is too high: " + line);
        }
    }

    private int getScoreByLine(int line) {
        return this.lines.size() - line - 1;
    }

    private String getLineByScore(int score) {
        return getLineByScore(this.lines, score);
    }

    private String getLineByScore(List<String> lines, int score) {
        return lines.get(lines.size() - score - 1);
    }

    private void sendObjectivePacket(ObjectiveMode mode) throws Throwable {
        Object packet = PACKET_SB_OBJ.invoke();

        setField(packet, String.class, this.id);
        setField(packet, int.class, mode.ordinal());

        if (mode != ObjectiveMode.REMOVE) {
            setComponentField(packet, this.title, 1);

            if (VersionType.V1_8.isHigherOrEqual()) {
                setField(packet, ENUM_SB_HEALTH_DISPLAY, ENUM_SB_HEALTH_DISPLAY_INTEGER);
            }
        } else if (VERSION_TYPE == VersionType.V1_7) {
            setField(packet, String.class, "", 1);
        }

        sendPacket(packet);
    }

    private void sendDisplayObjectivePacket() throws Throwable {
        Object packet = PACKET_SB_DISPLAY_OBJ.invoke();

        setField(packet, int.class, 1); // Position (1: sidebar)
        setField(packet, String.class, this.id); // Score Name

        sendPacket(packet);
    }

    private void sendScorePacket(int score, ScoreboardAction action) throws Throwable {
        Object packet = PACKET_SB_SCORE.invoke();

        setField(packet, String.class, COLOR_CODES[score], 0); // Player Name

        if (VersionType.V1_8.isHigherOrEqual()) {
            setField(packet, ENUM_SB_ACTION, action == ScoreboardAction.REMOVE ? ENUM_SB_ACTION_REMOVE : ENUM_SB_ACTION_CHANGE);
        } else {
            setField(packet, int.class, action.ordinal(), 1); // Action
        }

        if (action == ScoreboardAction.CHANGE) {
            setField(packet, String.class, this.id, 1); // Objective Name
            setField(packet, int.class, score); // Score
        }

        sendPacket(packet);
    }

    private void sendTeamPacket(int score, TeamMode mode) throws Throwable {
        if (mode == TeamMode.ADD_PLAYERS || mode == TeamMode.REMOVE_PLAYERS) {
            throw new UnsupportedOperationException();
        }

        int maxLength = hasLinesMaxLength() ? 16 : 1024;
        Object packet = PACKET_SB_TEAM.invoke();

        setField(packet, String.class, this.id + ':' + score); // Team name
        setField(packet, int.class, mode.ordinal(), VERSION_TYPE == VersionType.V1_8 ? 1 : 0); // Update mode

        if (mode == TeamMode.CREATE || mode == TeamMode.UPDATE) {
            String line = getLineByScore(score);
            String prefix;
            String suffix = null;

            if (line == null || line.isEmpty()) {
                prefix = COLOR_CODES[score] + ChatColor.RESET;
            } else if (line.length() <= maxLength) {
                prefix = line;
            } else {
                // Prevent splitting color codes
                int index = line.charAt(maxLength - 1) == ChatColor.COLOR_CHAR ? (maxLength - 1) : maxLength;
                prefix = line.substring(0, index);
                String suffixTmp = line.substring(index);
                ChatColor chatColor = null;

                if (suffixTmp.length() >= 2 && suffixTmp.charAt(0) == ChatColor.COLOR_CHAR) {
                    chatColor = ChatColor.getByChar(suffixTmp.charAt(1));
                }

                String color = ChatColor.getLastColors(prefix);
                boolean addColor = chatColor == null || chatColor.isFormat();

                suffix = (addColor ? (color.isEmpty() ? ChatColor.RESET.toString() : color) : "") + suffixTmp;
            }

            if (prefix.length() > maxLength || (suffix != null && suffix.length() > maxLength)) {
                // Something went wrong, just cut to prevent client crash/kick
                prefix = prefix.substring(0, maxLength);
                suffix = (suffix != null) ? suffix.substring(0, maxLength) : null;
            }

            if (VersionType.V1_17.isHigherOrEqual()) {
                Object team = PACKET_SB_SERIALIZABLE_TEAM.invoke();
                // Since the packet is initialized with null values, we need to change more things.
                setComponentField(team, "", 0); // Display name
                setField(team, CHAT_FORMAT_ENUM, RESET_FORMATTING); // Color
                setComponentField(team, prefix, 1); // Prefix
                setComponentField(team, suffix == null ? "" : suffix, 2); // Suffix
                setField(team, String.class, "always", 0); // Visibility
                setField(team, String.class, "always", 1); // Collisions
                setField(packet, Optional.class, Optional.of(team));
            } else {
                setComponentField(packet, prefix, 2); // Prefix
                setComponentField(packet, suffix == null ? "" : suffix, 3); // Suffix
                setField(packet, String.class, "always", 4); // Visibility for 1.8+
                setField(packet, String.class, "always", 5); // Collisions for 1.9+
            }

            if (mode == TeamMode.CREATE) {
                setField(packet, Collection.class, Collections.singletonList(COLOR_CODES[score])); // Players in the team
            }
        }

        sendPacket(packet);
    }

    private void sendPacket(Object packet) throws Throwable {
        if (this.deleted) {
            throw new IllegalStateException("This FastBoard is deleted");
        }

        if (this.player.isOnline()) {
            Object entityPlayer = PLAYER_GET_HANDLE.invoke(this.player);
            Object playerConnection = PLAYER_CONNECTION.invoke(entityPlayer);
            SEND_PACKET.invoke(playerConnection, packet);
        }
    }

    private void setField(Object object, Class<?> fieldType, Object value) throws ReflectiveOperationException {
        setField(object, fieldType, value, 0);
    }

    private void setField(Object packet, Class<?> fieldType, Object value, int count) throws ReflectiveOperationException {
        int i = 0;
        for (Field field : PACKETS.get(packet.getClass())) {
            if (field.getType() == fieldType && count == i++) {
                field.set(packet, value);
            }
        }
    }

    private void setComponentField(Object packet, String value, int count) throws Throwable {
        if (!VersionType.V1_13.isHigherOrEqual()) {
            setField(packet, String.class, value, count);
            return;
        }

        int i = 0;
        for (Field field : PACKETS.get(packet.getClass())) {
            if ((field.getType() == String.class || field.getType() == CHAT_COMPONENT_CLASS) && count == i++) {
                field.set(packet, value.isEmpty() ? EMPTY_MESSAGE : Array.get(MESSAGE_FROM_STRING.invoke(value), 0));
            }
        }
    }

    enum ObjectiveMode {
        CREATE, REMOVE, UPDATE
    }

    enum TeamMode {
        CREATE, REMOVE, UPDATE, ADD_PLAYERS, REMOVE_PLAYERS
    }

    enum ScoreboardAction {
        CHANGE, REMOVE
    }

    enum VersionType {
        V1_7, V1_8, V1_13, V1_17;

        public boolean isHigherOrEqual() {
            return VERSION_TYPE.ordinal() >= ordinal();
        }
    }

    private static String getPath(String path) {
        return Main.getScoreboardsConfiguration().getString(path);
    }

    private static List<String> getListPath(String path) {
        return Main.getScoreboardsConfiguration().getStringList(path);
    }

    private static Integer getInt(String path) {
        return Main.getScoreboardsConfiguration().getInt(path);
    }

    private static boolean getBoolean(String path) {
        return Main.getScoreboardsConfiguration().getBoolean(path);
    }

    private static void updateLobbyScoreboard(Player player) {
        String title = getPath("Scoreboards.Lobby.Title");
        List<String> lines = getListPath("Scoreboards.Lobby.Lines");
        List<String> newLines = new ArrayList<>();
        Scoreboard scoreboard = scoreboards.get(player.getUniqueId());
        if (scoreboard == null) {
            scoreboard = new Scoreboard(player);
            scoreboards.put(player.getUniqueId(), scoreboard);
        }
        scoreboard.updateTitle(Utils.colorize(title));
        for (String line : lines) {
            line = Utils.replace(line, "%player_name%", player.getName());
            line = Utils.replace(line, "%wins%", String.valueOf(ParkourRunAPI.getPlayerWins(player.getName())));
            line = Utils.replace(line, "%loses%", String.valueOf(ParkourRunAPI.getPlayerLoses(player.getName())));
            line = PlaceholderAPI.setPlaceholders(player, line);
            line = Utils.colorize(line);
            newLines.add(line);
        }
        scoreboard.updateLines(newLines);
    }

    private static void updateWaitingScoreboard(Player player) {
        Arena arena = ArenaManager.getArenaManager().getArena(player);
        String title = getPath("Scoreboards.Waiting.Title");
        List<String> lines = getListPath("Scoreboards.Waiting.Lines");
        List<String> newLines = new ArrayList<>();
        Scoreboard scoreboard = scoreboards.get(player.getUniqueId());
        if (scoreboard == null) {
            scoreboard = new Scoreboard(player);
            scoreboards.put(player.getUniqueId(), scoreboard);
        }
        scoreboard.updateTitle(Utils.colorize(title));
        for (String line : lines) {
            line = Utils.replace(line, "%player_name%", player.getName());
            line = Utils.replace(line, "%wins%", String.valueOf(ParkourRunAPI.getPlayerWins(player.getName())));
            line = Utils.replace(line, "%loses%", String.valueOf(ParkourRunAPI.getPlayerLoses(player.getName())));
            line = Utils.replace(line, "%arenaName%", ParkourRunAPI.getArenaDisplayName(arena.getArenaID()));
            line = PlaceholderAPI.setPlaceholders(player, line);
            line = Utils.colorize(line);
            newLines.add(line);
        }
        scoreboard.updateLines(newLines);
    }

    private static void updateStartingScoreboard(Player player) {
        Arena arena = ArenaManager.getArenaManager().getArena(player);
        String title = getPath("Scoreboards.Starting.Title");
        List<String> lines = getListPath("Scoreboards.Starting.Lines");
        List<String> newLines = new ArrayList<>();
        Scoreboard scoreboard = scoreboards.get(player.getUniqueId());
        if (scoreboard == null) {
            scoreboard = new Scoreboard(player);
            scoreboards.put(player.getUniqueId(), scoreboard);
        }
        scoreboard.updateTitle(Utils.colorize(title));
        for (String line : lines) {
            line = Utils.replace(line, "%player_name%", player.getName());
            line = Utils.replace(line, "%wins%", String.valueOf(ParkourRunAPI.getPlayerWins(player.getName())));
            line = Utils.replace(line, "%loses%", String.valueOf(ParkourRunAPI.getPlayerLoses(player.getName())));
            line = Utils.replace(line, "%arenaName%", ParkourRunAPI.getArenaDisplayName(arena.getArenaID()));
            line = Utils.replace(line, "%seconds%", String.valueOf(arena.getTime() + 1));
            line = PlaceholderAPI.setPlaceholders(player, line);
            line = Utils.colorize(line);
            newLines.add(line);
        }
        scoreboard.updateLines(newLines);
    }

    private static void updatePlayingScoreboard(Player player) {
        Arena arena = ArenaManager.getArenaManager().getArena(player);
        String title = getPath("Scoreboards.Playing.Title");
        List<String> lines = getListPath("Scoreboards.Playing.Lines");
        List<String> newLines = new ArrayList<>();
        Scoreboard scoreboard = scoreboards.get(player.getUniqueId());
        if (scoreboard == null) {
            scoreboard = new Scoreboard(player);
            scoreboards.put(player.getUniqueId(), scoreboard);
        }
        scoreboard.updateTitle(Utils.colorize(title));
        for (String line : lines) {
            line = Utils.replace(line, "%player_name%", player.getName());
            line = Utils.replace(line, "%wins%", String.valueOf(ParkourRunAPI.getPlayerWins(player.getName())));
            line = Utils.replace(line, "%loses%", String.valueOf(ParkourRunAPI.getPlayerLoses(player.getName())));
            line = Utils.replace(line, "%arenaName%", ParkourRunAPI.getArenaDisplayName(arena.getArenaID()));
            line = Utils.replace(line, "%time%", Utils.getFormattedTime(arena.getMaxTime() + 1));
            line = PlaceholderAPI.setPlaceholders(player, line);
            line = Utils.colorize(line);
            newLines.add(line);
        }
        scoreboard.updateLines(newLines);
    }

    private static void updateEndingScoreboard(Player player) {
        Arena arena = ArenaManager.getArenaManager().getArena(player);
        String title = getPath("Scoreboards.Ending.Title");
        List<String> lines = getListPath("Scoreboards.Ending.Lines");
        List<String> newLines = new ArrayList<>();
        Scoreboard scoreboard = scoreboards.get(player.getUniqueId());
        if (scoreboard == null) {
            scoreboard = new Scoreboard(player);
            scoreboards.put(player.getUniqueId(), scoreboard);
        }
        scoreboard.updateTitle(Utils.colorize(title));
        for (String line : lines) {
            line = Utils.replace(line, "%player_name%", player.getName());
            line = Utils.replace(line, "%wins%", String.valueOf(ParkourRunAPI.getPlayerWins(player.getName())));
            line = Utils.replace(line, "%loses%", String.valueOf(ParkourRunAPI.getPlayerLoses(player.getName())));
            line = Utils.replace(line, "%arenaName%", ParkourRunAPI.getArenaDisplayName(arena.getArenaID()));
            line = arena.getWinner() != null ? Utils.replace(line, "%winner%", arena.getWinner().getName()) : Utils.replace(line, "%winner%", Main.getMessagesConfiguration().getString("Messages.Arena.Nobody"));
            line = PlaceholderAPI.setPlaceholders(player, line);
            line = Utils.colorize(line);
            newLines.add(line);
        }
        scoreboard.updateLines(newLines);
    }

    public static void run() {
        BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
        int lobbyUpdate = getInt("Scoreboards.Lobby.Update");
        int waitingUpdate = getInt("Scoreboards.Waiting.Update");
        int startingUpdate = getInt("Scoreboards.Starting.Update");
        int playingUpdate = getInt("Scoreboards.Playing.Update");
        int endingUpdate = getInt("Scoreboards.Ending.Update");

        if (getBoolean("Scoreboards.Lobby.Enabled")) {
            scheduler.scheduleSyncRepeatingTask(Main.getInstance(), () -> {
                for(Player player : Bukkit.getOnlinePlayers()) {
                    Arena arena = ArenaManager.getArenaManager().getArena(player);
                    if (arena == null) {
                        updateLobbyScoreboard(player);
                    }
                }
            },0, lobbyUpdate);
        }

        if (getBoolean("Scoreboards.Waiting.Enabled")) {
            scheduler.scheduleSyncRepeatingTask(Main.getInstance(), () -> {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    Arena arena = ArenaManager.getArenaManager().getArena(player);
                    if (arena == null)
                        return;
                    if (arena.getArenaStatus() == ArenaStatus.WAITING) {
                        updateWaitingScoreboard(player);
                    }
                }
            }, 0, waitingUpdate);
        }

        if (getBoolean("Scoreboards.Starting.Enabled")) {
            scheduler.scheduleSyncRepeatingTask(Main.getInstance(), () -> {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    Arena arena = ArenaManager.getArenaManager().getArena(player);
                    if (arena == null)
                        return;
                    if (arena.getArenaStatus() == ArenaStatus.STARTING) {
                        updateStartingScoreboard(player);
                    }
                }
            }, 0, startingUpdate);
        }

        if (getBoolean("Scoreboards.Playing.Enabled")) {
            scheduler.scheduleSyncRepeatingTask(Main.getInstance(), () -> {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    Arena arena = ArenaManager.getArenaManager().getArena(player);
                    if (arena == null)
                        return;
                    if (arena.getArenaStatus() == ArenaStatus.PLAYING) {
                        updatePlayingScoreboard(player);
                    }
                }
            }, 0, playingUpdate);
        }

        if (getBoolean("Scoreboards.Ending.Enabled")) {
            scheduler.scheduleSyncRepeatingTask(Main.getInstance(), () -> {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    Arena arena = ArenaManager.getArenaManager().getArena(player);
                    if (arena == null)
                        return;
                    if (arena.getArenaStatus() == ArenaStatus.ENDING) {
                        updateEndingScoreboard(player);
                    }
                }
            }, 0, endingUpdate);
        }

        scheduler.scheduleSyncRepeatingTask(Main.getInstance(), () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                Scoreboard scoreboard = scoreboards.get(player.getUniqueId());
                Arena arena = ArenaManager.getArenaManager().getArena(player);
                if (arena == null && !getBoolean("Scoreboards.Lobby.Enabled")) {
                    scoreboards.remove(player.getUniqueId());
                    scoreboard.delete();
                    return;
                }
                if (scoreboard != null) {
                    if (arena != null && arena.getArenaStatus() == ArenaStatus.WAITING && !getBoolean("Scoreboards.Waiting.Enabled")) {
                        scoreboards.remove(player.getUniqueId());
                        scoreboard.delete();
                        return;
                    }
                    if (arena != null && arena.getArenaStatus() == ArenaStatus.STARTING && !getBoolean("Scoreboards.Starting.Enabled")) {
                        scoreboards.remove(player.getUniqueId());
                        scoreboard.delete();
                        return;
                    }
                    if (arena != null && arena.getArenaStatus() == ArenaStatus.PLAYING && !getBoolean("Scoreboards.Playing.Enabled")) {
                        scoreboards.remove(player.getUniqueId());
                        scoreboard.delete();
                        return;
                    }
                    if (arena != null && arena.getArenaStatus() == ArenaStatus.ENDING && !getBoolean("Scoreboards.Ending.Enabled")) {
                        scoreboards.remove(player.getUniqueId());
                        scoreboard.delete();
                    }
                }
            }
        }, 0, 20);
    }
}
