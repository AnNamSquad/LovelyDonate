package org.lovelysmp.lovelypay.handler.data;

import org.lovelysmp.lovelypay.handler.ICoins;
import org.lovelysmp.lovelypay.handler.coins.CoinsEngineHandler;
import org.lovelysmp.lovelypay.handler.coins.DefaultCoinsHandler;
import org.lovelysmp.lovelypay.handler.coins.PlayerPointsHandler;

public enum CoinsAPI {
    PLAYERPOINTS(PlayerPointsHandler.class),
    NONE(DefaultCoinsHandler.class),
    COINSENGINE(CoinsEngineHandler.class);

    public final Class<?> handlerClass;

    CoinsAPI(Class<? extends ICoins> handlerClass) {
        this.handlerClass = handlerClass;
    }
}
