package io.thadow.parkourrun.utils.lib.scoreboard;

import io.thadow.parkourrun.utils.lib.scoreboard.IPlayerBoard;
import io.thadow.parkourrun.utils.lib.scoreboard.Reflection;
import io.thadow.parkourrun.utils.lib.scoreboard.Scoreboard;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Objective;

public class PlayerBoard implements IPlayerBoard<String, Integer, String> {
    private Player player;
    private org.bukkit.scoreboard.Scoreboard scoreboard;
    private String name;
    private Objective objective;
    private Objective buffer;
    private Map<Integer, String> lines = new HashMap<Integer, String>();
    private boolean deleted = false;

    public PlayerBoard(Player player, org.bukkit.scoreboard.Scoreboard scoreboard, String name) {
        this.player = player;
        this.scoreboard = scoreboard;
        if (this.scoreboard == null) {
            org.bukkit.scoreboard.Scoreboard sb = player.getScoreboard();
            if (sb == null || sb == Bukkit.getScoreboardManager().getMainScoreboard()) {
                sb = Bukkit.getScoreboardManager().getNewScoreboard();
            }
            this.scoreboard = sb;
        }
        this.name = name;
        String subName = player.getName().length() <= 14 ? player.getName() : player.getName().substring(0, 14);
        this.objective = this.scoreboard.getObjective("sb" + subName);
        this.buffer = this.scoreboard.getObjective("bf" + subName);
        if (this.objective == null) {
            this.objective = this.scoreboard.registerNewObjective("sb" + subName, "dummy");
        }
        if (this.buffer == null) {
            this.buffer = this.scoreboard.registerNewObjective("bf" + subName, "dummy");
        }
        this.objective.setDisplayName(name);
        this.sendObjective(this.objective, ObjectiveMode.CREATE);
        this.sendObjectiveDisplay(this.objective);
        this.buffer.setDisplayName(name);
        this.sendObjective(this.buffer, ObjectiveMode.CREATE);
        this.player.setScoreboard(this.scoreboard);
    }

    @Override
    public String get(Integer score) {
        if (this.deleted) {
            throw new IllegalStateException("The PlayerBoard is deleted!");
        }
        return this.lines.get(score);
    }

    @Override
    public void set(String name, Integer score) {
        if (this.deleted) {
            throw new IllegalStateException("The PlayerBoard is deleted!");
        }
        String oldName = this.lines.get(score);
        if (name.equals(oldName)) {
            return;
        }
        this.lines.entrySet().removeIf(entry -> ((String)entry.getValue()).equals(name));
        if (oldName != null) {
            if (Reflection.getVersion().getMajor().equals("1.7")) {
                this.sendScore(this.objective, oldName, score, true);
                this.sendScore(this.objective, name, score, false);
            } else {
                this.sendScore(this.buffer, oldName, score, true);
                this.sendScore(this.buffer, name, score, false);
                this.swapBuffers();
                this.sendScore(this.buffer, oldName, score, true);
                this.sendScore(this.buffer, name, score, false);
            }
        } else {
            this.sendScore(this.objective, name, score, false);
            this.sendScore(this.buffer, name, score, false);
        }
        this.lines.put(score, name);
    }

    public void setAll(String ... lines) {
        if (this.deleted) {
            throw new IllegalStateException("The PlayerBoard is deleted!");
        }
        for (int i = 0; i < lines.length; ++i) {
            String line = lines[i];
            this.set(line, lines.length - i);
        }
        HashSet<Integer> scores = new HashSet<Integer>(this.lines.keySet());
        Iterator iterator = scores.iterator();
        while (iterator.hasNext()) {
            int score = (Integer)iterator.next();
            if (score > 0 && score <= lines.length) continue;
            this.remove(score);
        }
    }

    @Override
    public void clear() {
        new HashSet<Integer>(this.lines.keySet()).forEach(this::remove);
        this.lines.clear();
    }

    private void swapBuffers() {
        this.sendObjectiveDisplay(this.buffer);
        Objective temp = this.buffer;
        this.buffer = this.objective;
        this.objective = temp;
    }

    private void sendObjective(Objective obj, ObjectiveMode mode) {
        try {
            Object objHandle = Reflection.getHandle((Object)obj);
            Object packetObj = Reflection.PACKET_OBJ.newInstance(objHandle, mode.ordinal());
            Reflection.sendPacket(packetObj, this.player);
        }
        catch (IllegalAccessException | InstantiationException | NoSuchMethodException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    private void sendObjectiveDisplay(Objective obj) {
        try {
            Object objHandle = Reflection.getHandle((Object)obj);
            Object packet = Reflection.PACKET_DISPLAY.newInstance(1, objHandle);
            Reflection.sendPacket(packet, this.player);
        }
        catch (IllegalAccessException | InstantiationException | NoSuchMethodException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    private void sendScore(Objective obj, String name, int score, boolean remove) {
        try {
            Object sbHandle = Reflection.getHandle((Object)this.scoreboard);
            Object objHandle = Reflection.getHandle((Object)obj);
            Object sbScore = Reflection.SB_SCORE.newInstance(sbHandle, objHandle, name);
            Reflection.SB_SCORE_SET.invoke(sbScore, score);
            Map scores = (Map)Reflection.PLAYER_SCORES.get(sbHandle);
            if (remove) {
                if (scores.containsKey(name)) {
                    ((Map)scores.get(name)).remove(objHandle);
                }
            } else {
                if (!scores.containsKey(name)) {
                    scores.put(name, new HashMap());
                }
                ((Map)scores.get(name)).put(objHandle, sbScore);
            }
            switch (Reflection.getVersion().getMajor()) {
                case "1.7": {
                    Object packet = Reflection.PACKET_SCORE.newInstance(sbScore, remove ? 1 : 0);
                    Reflection.sendPacket(packet, this.player);
                    break;
                }
                case "1.8":
                case "1.9":
                case "1.10":
                case "1.11":
                case "1.12": {
                    Object packet = remove ? Reflection.PACKET_SCORE_REMOVE.newInstance(name, objHandle) : Reflection.PACKET_SCORE.newInstance(sbScore);
                    Reflection.sendPacket(packet, this.player);
                    break;
                }
                default: {
                    Object packet = Reflection.PACKET_SCORE.newInstance(remove ? Reflection.ENUM_SCORE_ACTION_REMOVE : Reflection.ENUM_SCORE_ACTION_CHANGE, obj.getName(), name, score);
                    Reflection.sendPacket(packet, this.player);
                    break;
                }
            }
        }
        catch (IllegalAccessException | InstantiationException | NoSuchMethodException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void remove(Integer score) {
        if (this.deleted) {
            throw new IllegalStateException("The PlayerBoard is deleted!");
        }
        String name = this.lines.get(score);
        if (name == null) {
            return;
        }
        this.scoreboard.resetScores(name);
        this.lines.remove(score);
    }

    @Override
    public void delete() {
        if (this.deleted) {
            return;
        }
        Scoreboard.instance().removeBoard(this.player);
        this.sendObjective(this.objective, ObjectiveMode.REMOVE);
        this.sendObjective(this.buffer, ObjectiveMode.REMOVE);
        this.objective.unregister();
        this.objective = null;
        this.buffer.unregister();
        this.buffer = null;
        this.lines = null;
        this.deleted = true;
    }

    @Override
    public Map<Integer, String> getLines() {
        if (this.deleted) {
            throw new IllegalStateException("The PlayerBoard is deleted!");
        }
        return new HashMap<Integer, String>(this.lines);
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void setName(String name) {
        if (this.deleted) {
            throw new IllegalStateException("The PlayerBoard is deleted!");
        }
        this.name = name;
        this.objective.setDisplayName(name);
        this.buffer.setDisplayName(name);
        this.sendObjective(this.objective, ObjectiveMode.UPDATE);
        this.sendObjective(this.buffer, ObjectiveMode.UPDATE);
    }

    public Player getPlayer() {
        return this.player;
    }

    public org.bukkit.scoreboard.Scoreboard getScoreboard() {
        return this.scoreboard;
    }

    private static enum ObjectiveMode {
        CREATE,
        REMOVE,
        UPDATE;

    }
}