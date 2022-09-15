package io.thadow.parkourrun.utils.storage.type.mysql;

import io.thadow.parkourrun.data.PlayerData;

public interface Callback {

    public void onEnd(PlayerData playerData);
}
