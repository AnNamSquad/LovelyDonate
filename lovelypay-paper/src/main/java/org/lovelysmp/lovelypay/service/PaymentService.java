package org.lovelysmp.lovelypay.service;

import lombok.Getter;
import org.lovelysmp.lovelypay.LPPlugin;
import org.lovelysmp.lovelypay.config.ConfigManager;
import org.lovelysmp.lovelypay.config.types.BankingConfig;
import org.lovelysmp.lovelypay.config.types.CardConfig;
import org.lovelysmp.lovelypay.data.PaymentStatus;
import org.lovelysmp.lovelypay.handler.HandlerRegistry;
import org.lovelysmp.lovelypay.handler.data.BankAPI;
import org.lovelysmp.lovelypay.handler.data.CardAPI;
import org.lovelysmp.lovelypay.model.Payment;
import org.lovelysmp.lovelypay.util.MessageUtil;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class PaymentService implements IService {


    private final ConcurrentHashMap<UUID, Payment> pollingPayments = new ConcurrentHashMap<>(); // payment id is key
    private final ConcurrentHashMap<UUID, Payment> payments = new ConcurrentHashMap<>(); // payment id is key
    private final ConcurrentHashMap<UUID, UUID> playerBankingSessionPayment = new ConcurrentHashMap<>(); // Store player uuid and payment id
    private final ConcurrentHashMap<UUID, byte[]> playerBankQRCode = new ConcurrentHashMap<>(); // Store player uuid and VietQR map bytew
    private HandlerRegistry handlerRegistry;

    // use for storing data and pulling data out of the db later on
    public static BankAPI getBankAPI() {
        BankingConfig bankingConfig = ConfigManager.getInstance().getConfig(BankingConfig.class);
        return bankingConfig.bankApi;
    }

    public static CardAPI getCardAPI() {
        CardConfig cardConfig = ConfigManager.getInstance().getConfig(CardConfig.class);
        return cardConfig.cardApi;
    }

    @Override
    public void setup() {
        handlerRegistry = new HandlerRegistry();
    }

    @Override
    public void shutdown() {

    }

    public PaymentStatus sendCard(Payment payment) {
        PaymentStatus status = handlerRegistry.getCardHandler().processPayment(payment);
        if (status == PaymentStatus.PENDING) {
            payments.putIfAbsent(payment.getPaymentID(), payment);
            return status;
        }
        return status;
    }

    public PaymentStatus sendBank(Payment payment) {

        PaymentStatus status = handlerRegistry.getBankHandler().processPayment(payment);
        if (status == PaymentStatus.PENDING) {
            payments.putIfAbsent(payment.getPaymentID(), payment);
            return status;
        }
        return status;
    }

    public void clearPlayerBankCache(UUID playerUUID) {
        playerBankQRCode.remove(playerUUID);
        playerBankingSessionPayment.remove(playerUUID);
    }

    public void cancelBankPayment(UUID playerUUID) {
        UUID paymentID = playerBankingSessionPayment.get(playerUUID);
        int retryCount = 0;
        boolean cancelled = false;
        if (paymentID == null) {
            MessageUtil.debug("[PaymentService-Cancel] No payment found for " + playerUUID);
            return;
        }
        while (retryCount < 5 && !cancelled) {
            PaymentStatus status = handlerRegistry.getBankHandler().cancel(payments.get(paymentID)); // call to cancel payment

            if (status == PaymentStatus.CANCELLED) {
                MessageUtil.debug("[PaymentService-Cancel] " + payments.get(paymentID));
                cancelled = true;
            } else {
                MessageUtil.debug("[PaymentService-Cancel] " + payments.get(paymentID) + " failed to cancel, retrying...");
                retryCount++;
            }
        }

        if (!cancelled) {
            LPPlugin.getInstance().getLogger().info("[PaymentService-Cancel] Max retries reached for " + payments.get(paymentID));
        }

        payments.remove(paymentID); // remove payment from existing payment on the server
        pollingPayments.remove(paymentID); // remove payment from polling payments
        playerBankingSessionPayment.remove(playerUUID);
        playerBankQRCode.remove(playerUUID);
    }

}
