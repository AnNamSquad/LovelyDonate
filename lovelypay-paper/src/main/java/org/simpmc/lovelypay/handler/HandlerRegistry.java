package org.simpmc.lovelypay.handler;

import lombok.Getter;
import org.simpmc.lovelypay.config.ConfigManager;
import org.simpmc.lovelypay.config.types.BankingConfig;
import org.simpmc.lovelypay.config.types.CardConfig;
import org.simpmc.lovelypay.config.types.CoinsConfig;
import org.simpmc.lovelypay.handler.coins.DefaultCoinsHandler;
import org.simpmc.lovelypay.util.MessageUtil;

import java.lang.reflect.InvocationTargetException;

@Getter
public class HandlerRegistry {

    private PaymentHandler cardHandler;
    private PaymentHandler bankHandler;

    private CoinsHandler coinsHandler;

    public HandlerRegistry() {
        try {
            init();
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException |
                 IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    // Only call this once
    private void init() throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {

        CardConfig cardConfig = ConfigManager.getInstance().getConfig(CardConfig.class);
        BankingConfig bankingConfig = ConfigManager.getInstance().getConfig(BankingConfig.class);
        CoinsConfig coinsConfig = ConfigManager.getInstance().getConfig(CoinsConfig.class);

        cardHandler = cardConfig.cardApi.handlerClass.getDeclaredConstructor().newInstance();
        bankHandler = bankingConfig.bankApi.handlerClass.getDeclaredConstructor().newInstance();
        try {
            coinsHandler = (CoinsHandler) coinsConfig.pointsProvider.handlerClass.getDeclaredConstructor().newInstance();
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            MessageUtil.warn("Unable to find any compatible Points plugin provider, voiding all coins manipulation");
            coinsHandler = new DefaultCoinsHandler();
        }
        MessageUtil.info("Registered handlers: ");
        MessageUtil.info("Card Handler: " + cardHandler.getClass().getSimpleName());
        MessageUtil.info("Bank Handler: " + bankHandler.getClass().getSimpleName());
        MessageUtil.info("Coins Handler: " + coinsHandler.getClass().getSimpleName());
    }

    public void reload() {
        try {
            init();
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException |
                 IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

}
