package org.simpmc.lovelypay.handler.data;

import org.simpmc.lovelypay.handler.ICoins;
import org.simpmc.lovelypay.handler.coins.CoinsEngineHandler;
import org.simpmc.lovelypay.handler.coins.DefaultCoinsHandler;
import org.simpmc.lovelypay.handler.coins.PlayerPointsHandler;

public enum CoinsAPI {
    PLAYERPOINTS(PlayerPointsHandler.class),
    NONE(DefaultCoinsHandler.class),
    COINSENGINE(CoinsEngineHandler.class);

    public final Class<?> handlerClass;

    CoinsAPI(Class<? extends ICoins> handlerClass) {
        this.handlerClass = handlerClass;
    }
}
