package io.thadow.parkourrun.utils.storage;

import io.thadow.parkourrun.utils.storage.type.LocalStorage;
import org.bukkit.entity.Player;

public class Storage {
    private StorageType type;
    private static final Storage storage = new Storage();


    public void addWin(Player player) {
        if (getStorageType() == StorageType.LOCAL) {
            LocalStorage.addWin(player);
        }
    }

    public void addLose(Player player) {
        if (getStorageType() == StorageType.LOCAL) {
            LocalStorage.addLose(player);
        }
    }

    public void createPlayer(Player target) {
        if (getStorageType() == StorageType.LOCAL) {
            if (!LocalStorage.containsPlayer(target)) {
                LocalStorage.createPlayerData(target);
            }
        }
    }

    public void removePlayer(Player sender, Player target) {
        if (getStorageType() == StorageType.LOCAL) {
            if (!LocalStorage.containsPlayer(target)) {
                sender.sendMessage("El jugador no existe");
            } else {
                LocalStorage.removePlayerData(target);
                sender.sendMessage("Jugador removido correctamente");
            }
        }
    }

    public void setupStorage(StorageType type) {
        if (type == StorageType.LOCAL) {
            this.type = StorageType.LOCAL;
            LocalStorage.register();
        }
    }

    public StorageType getStorageType() {
        return type;
    }

    public static Storage getStorage() {
        return storage;
    }
}
