package org.simpmc.simppay.handler.coins;

import org.simpmc.simppay.handler.CoinsHandler;

import java.util.UUID;

public class PlayerPointsHandler extends CoinsHandler {
    private org.black_ixx.playerpoints.PlayerPointsAPI ppApi;

    public PlayerPointsHandler() {
        this.isAsync = true;
        this.ppApi = org.black_ixx.playerpoints.PlayerPoints.getInstance().getAPI();
    }

    @Override
    public void take(UUID uuid, int amount) {
        ppApi.take(uuid, amount);
    }

    @Override
    public int look(UUID uuid) {
        return ppApi.look(uuid);
    }

    @Override
    public void give(UUID uuid, int amount) {
        ppApi.give(uuid, amount);
    }

    @Override
    public void set(UUID uuid, int amount) {
        ppApi.set(uuid, amount);
    }
}
