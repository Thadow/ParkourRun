package io.thadow.parkourrun.listeners;

import io.thadow.parkourrun.Main;
import io.thadow.parkourrun.arena.Arena;
import io.thadow.parkourrun.arena.status.ArenaStatus;
import io.thadow.parkourrun.managers.ArenaManager;
import io.thadow.parkourrun.managers.CheckpointManager;
import io.thadow.parkourrun.utils.Region;
import io.thadow.parkourrun.utils.Utils;
import io.thadow.parkourrun.utils.storage.ActionCooldown;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;
import org.bukkit.util.Vector;

public class ArenaListener implements Listener {

    @EventHandler
    public void checkPlayerWin(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (ArenaManager.getArenaManager().getArena(player) != null) {
            Arena arena = ArenaManager.getArenaManager().getArena(player);
            if (arena.getArenaStatus() != ArenaStatus.PLAYING) {
                return;
            }
            String[] winCorner1 = arena.getWinCorner1().split(";");
            String[] winCorner2 = arena.getWinCorner2().split(";");
            Region winRegion = new Region(new Vector(Integer.parseInt(winCorner1[0]), Integer.parseInt(winCorner1[1]), Integer.parseInt(winCorner1[2])),
                    new Vector(Integer.parseInt(winCorner2[0]), Integer.parseInt(winCorner2[1]), Integer.parseInt(winCorner2[2])));
            if (winRegion.isInside(player.getLocation())) {
                if (arena.getConfig().getBoolean("Extensions.Checkpoints.Need All To Win")) {
                    int lastCheckpoint = CheckpointManager.getCheckpointManager().getTotalCheckpoints(arena);
                    int currentPlayerCheckpoint = CheckpointManager.getCheckpointManager().getPlayerCurrentCheckpoint(player);
                    if (currentPlayerCheckpoint == lastCheckpoint) {
                        arena.finalizeArenaWithWinner(player);
                    } else {
                        if (ActionCooldown.isOnCooldown("cantWinMessage", player))
                            return;
                        player.sendMessage("Necesitas todos los checkpoints para ganar!");
                        ActionCooldown.addCooldown("cantWinMessage", player, 5);
                    }
                } else {
                    arena.finalizeArenaWithWinner(player);
                }
            }
        }
    }

    @EventHandler
    public void checkPlayerZone(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (ArenaManager.getArenaManager().getArena(player) != null) {
            Arena arena = ArenaManager.getArenaManager().getArena(player);
            String[] arenaZoneCorner1 = arena.getArenaCorner1().split(";");
            String[] arenaZoneCorner2 = arena.getArenaCorner2().split(";");
            Region arenaRegion = new Region(new Vector(Integer.parseInt(arenaZoneCorner1[0]), Integer.parseInt(arenaZoneCorner1[1]), Integer.parseInt(arenaZoneCorner1[2])),
                    new Vector(Integer.parseInt(arenaZoneCorner2[0]), Integer.parseInt(arenaZoneCorner2[1]), Integer.parseInt(arenaZoneCorner2[2])));
            if (!arenaRegion.isInside(player.getLocation())) {
                if (arena.getArenaStatus() == ArenaStatus.PLAYING) {
                    int currentCheckpointID = CheckpointManager.getCheckpointManager().getPlayerCurrentCheckpoint(player);
                    if (currentCheckpointID == 0) {
                        player.teleport(arena.getSpawn());
                        return;
                    }
                    Location location = CheckpointManager.getCheckpointManager().getCheckpointLocation(arena, currentCheckpointID);
                    player.teleport(location);
                } else if (arena.getArenaStatus() == ArenaStatus.WAITING || arena.getArenaStatus() == ArenaStatus.STARTING) {
                    player.teleport(arena.getWaitLocation());
                } else if (arena.getArenaStatus() == ArenaStatus.ENDING) {
                    player.teleport(arena.getSpawn());
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void checkPlayerCheckpoint(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (ArenaManager.getArenaManager().getArena(player) != null) {
            if (!event.getAction().equals(Action.PHYSICAL))
                return;
            Material clickedBlock = event.getClickedBlock().getType();
            Arena arena = ArenaManager.getArenaManager().getArena(player);
            String plate = Main.getConfiguration().getString("Configuration.Arenas.Checkpoints.Plate");
            if (clickedBlock == Material.valueOf(plate)) {
                String underBlock = Main.getConfiguration().getString("Configuration.Arenas.Checkpoints.Under Block");
                if (Utils.isNearBlock(player, 2, underBlock)) {
                    int currentCheckpointID = CheckpointManager.getCheckpointManager().getPlayerCurrentCheckpoint(player);
                    if (currentCheckpointID == 0) {
                        String[] corners = arena.getCheckpointCorners(1).split("/-/");
                        if (corners[1] == null || corners[2] == null) {
                            return;
                        }
                        String[] corner1 = corners[1].split(";");
                        String[] corner2 = corners[2].split(";");
                        Region region = new Region(new Vector(Integer.parseInt(corner1[0]), Integer.parseInt(corner1[1]), Integer.parseInt(corner1[2])),
                                new Vector(Integer.parseInt(corner2[0]), Integer.parseInt(corner2[1]), Integer.parseInt(corner2[2])));
                        if (region.isInside(player.getLocation())) {
                            CheckpointManager.getCheckpointManager().setPlayerCurrentCheckpoint(arena, player, 1);
                            CheckpointManager.getCheckpointManager().setPlayerNextCheckpoint(arena, player, 2);
                            player.sendMessage("Checkpoint: " + CheckpointManager.getCheckpointManager().getPlayerCurrentCheckpoint(player));
                        }
                    } else {
                        int nextPlayerCheckpoint = CheckpointManager.getCheckpointManager().getPlayerNextCheckpoint(player);
                        if (!arena.getCheckpoints().containsKey(nextPlayerCheckpoint)) {
                            return;
                        }
                        if ((currentCheckpointID + 1) == nextPlayerCheckpoint) {
                            String[] corners = arena.getCheckpointCorners(nextPlayerCheckpoint).split("/-/");
                            if (corners[1] == null || corners[2] == null) {
                                return;
                            }
                            String[] corner1 = corners[1].split(";");
                            String[] corner2 = corners[2].split(";");
                            Region region = new Region(new Vector(Integer.parseInt(corner1[0]), Integer.parseInt(corner1[1]), Integer.parseInt(corner1[2])),
                                    new Vector(Integer.parseInt(corner2[0]), Integer.parseInt(corner2[1]), Integer.parseInt(corner2[2])));
                            if (region.isInside(player.getLocation())) {
                                CheckpointManager.getCheckpointManager().setPlayerCurrentCheckpoint(arena, player, currentCheckpointID + 1);
                                CheckpointManager.getCheckpointManager().setPlayerNextCheckpoint(arena, player, nextPlayerCheckpoint + 1);
                                player.sendMessage("Checkpoint: " + CheckpointManager.getCheckpointManager().getPlayerCurrentCheckpoint(player));
                            }
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPlayerBreakBlock(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (ArenaManager.getArenaManager().getArena(player) != null) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerPlaceBlock(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        if (ArenaManager.getArenaManager().getArena(player) != null) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerFallDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            if (event.getCause() == EntityDamageEvent.DamageCause.FALL) {
                Arena arena = ArenaManager.getArenaManager().getArena((Player) event.getEntity());
                if (arena != null) {
                    if (arena.getConfig().getBoolean("Extensions.Damage.Disable Fall Damage")) {
                        event.setCancelled(true);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            if (event.getCause() != EntityDamageEvent.DamageCause.FALL) {
                Player player = (Player) event.getEntity();
                Arena arena = ArenaManager.getArenaManager().getArena(player);
                if (arena != null) {
                    if (arena.getConfig().getBoolean("Extensions.Damage.Disable Player Damage")) {
                        event.setCancelled(true);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPlayerDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            Arena arena = ArenaManager.getArenaManager().getArena(player);
            if (arena != null) {
                if (arena.getConfig().getBoolean("Extensions.Damage.Disable Monster Damage")) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onPlayerFoodLevelChange(FoodLevelChangeEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            if (ArenaManager.getArenaManager().getArena(player) != null) {
                event.setFoodLevel(20);
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerDeathEvent(PlayerDeathEvent event) {
        Player player = event.getEntity();
        if (ArenaManager.getArenaManager().getArena(player) != null) {
            player.spigot().respawn();
        }
    }

    @EventHandler
    public void onPlayerRespawnEvent(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        if (ArenaManager.getArenaManager().getArena(player) != null) {
            Arena arena = ArenaManager.getArenaManager().getArena(player);
            if (arena.getArenaStatus() == ArenaStatus.PLAYING || arena.getArenaStatus() == ArenaStatus.ENDING) {
                Location spawnLocation = ArenaManager.getArenaManager().getArena(player).getSpawn();
                Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> player.teleport(spawnLocation), 20L);
            } else {
                Location waitLocation = ArenaManager.getArenaManager().getArena(player).getWaitLocation();
                Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> player.teleport(waitLocation), 20L);
            }
        }
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        if (ArenaManager.getArenaManager().getArena(player) != null) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerPickUpItem(PlayerPickupItemEvent event) {
        Player player = event.getPlayer();
        if (ArenaManager.getArenaManager().getArena(player) != null) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerChatEvent(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        if (event.isCancelled()) {
            return;
        }
        if (Main.getConfiguration().getBoolean("Configuration.Arenas.Per Arena Chat")) {
            Arena arena = ArenaManager.getArenaManager().getArena(player);
            if (arena != null) {
                for (Player players : Bukkit.getOnlinePlayers()) {
                    Arena arenap = ArenaManager.getArenaManager().getArena(players);
                    if (arenap == null) {
                        event.getRecipients().remove(players);
                    } else {
                        if (!arenap.getArenaID().equals(arena.getArenaID())) {
                            event.getRecipients().remove(players);
                        }
                    }
                }
            } else {
                for (Player players : Bukkit.getOnlinePlayers()) {
                    if (ArenaManager.getArenaManager().getArena(players) != null) {
                        event.getRecipients().remove(players);
                    }
                }
            }
        }
    }


    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (ArenaManager.getArenaManager().getArena(player) != null) {
            Arena arena = ArenaManager.getArenaManager().getArena(player);
            ArenaManager.getArenaManager().removePlayer(player, arena.getArenaStatus() == ArenaStatus.ENDING);
        }
    }
}
