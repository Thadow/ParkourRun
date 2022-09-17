package io.thadow.parkourrun.api;

import io.thadow.parkourrun.Main;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class PAPIExpansion extends PlaceholderExpansion {
    private Main main;

    public PAPIExpansion(Main main) {
        this.main = main;
    }


    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "pkr";
    }

    @Override
    public @NotNull String getAuthor() {
        return "Thadow";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0";
    }


    @Override
    public @Nullable String onPlaceholderRequest(Player player, @NotNull String identifier) {
        if (player == null) {
            return "";
        }

        if (identifier.equals("wins")) {
            return String.valueOf(ParkourRunAPI.getPlayerWins(player.getName()));
        }

        if (identifier.equals("loses")) {
            return String.valueOf(ParkourRunAPI.getPlayerLoses(player.getName()));
        }

        if (identifier.startsWith("arena_status_")) {
            String arenaID = identifier.replace("arena_status_", "");
            return Objects.requireNonNull(ParkourRunAPI.getArenaStatus(arenaID)).toString();
        }

        if(identifier.startsWith("arena_name_")) {
            String arenaID = identifier.replace("arena_name_", "");
            return ParkourRunAPI.getArenaDisplayName(arenaID);
        }

        if (identifier.startsWith("arena_total_players_")) {
            String arenaID = identifier.replace("arena_total_players_", "");
            return String.valueOf(ParkourRunAPI.getArenaTotalPlayers(arenaID));
        }

        return null;
    }
}
