package org.lovelysmp.lovelypay.handler.coins;

import org.lovelysmp.lovelypay.config.ConfigManager;
import org.lovelysmp.lovelypay.config.types.CoinsConfig;
import org.lovelysmp.lovelypay.handler.CoinsHandler;
import su.nightexpress.coinsengine.api.CoinsEngineAPI;
import su.nightexpress.coinsengine.api.currency.Currency;

import java.util.UUID;

public class CoinsEngineHandler extends CoinsHandler {

    private final Currency currency;

    public CoinsEngineHandler() {
        this.isAsync = false;
        this.currency = CoinsEngineAPI.getCurrency(ConfigManager.getInstance().getConfig(CoinsConfig.class).coinsEngineCurrency);
    }

    @Override
    public void take(UUID uuid, int amount) {
        CoinsEngineAPI.removeBalance(uuid, currency, amount);
    }

    @Override
    public int look(UUID uuid) {
        return (int) CoinsEngineAPI.getBalance(uuid, currency);
    }

    @Override
    public void give(UUID uuid, int amount) {
        CoinsEngineAPI.addBalance(uuid, currency, amount);
    }

    @Override
    public void set(UUID uuid, int amount) {
        CoinsEngineAPI.setBalance(uuid, currency, amount);
    }
}
