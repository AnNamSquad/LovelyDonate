package org.simpmc.simppay.handler.card;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.bukkit.Bukkit;
import org.simpmc.simppay.config.ConfigManager;
import org.simpmc.simppay.config.types.card.Gachthe1sConfig;
import org.simpmc.simppay.data.PaymentStatus;
import org.simpmc.simppay.data.card.CardType;
import org.simpmc.simppay.event.PaymentQueueSuccessEvent;
import org.simpmc.simppay.handler.CardHandler;
import org.simpmc.simppay.model.Payment;
import org.simpmc.simppay.model.PaymentResult;
import org.simpmc.simppay.model.detail.CardDetail;
import org.simpmc.simppay.model.detail.PaymentDetail;
import org.simpmc.simppay.util.HashUtil;
import org.simpmc.simppay.util.MessageUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class GT1SHandler extends CardHandler {
    // Gachthe1s.com
    String GT1S_CREATE_URL = "https://gachthe1s.com//chargingws/v2";
    String GT1S_GET_STATUS_URL = "https://gachthe1s.com/chargingws/v2";
    String PARTNER_ID = ConfigManager.getInstance().getConfig(Gachthe1sConfig.class).partnerId;
    String PARTNER_KEY = ConfigManager.getInstance().getConfig(Gachthe1sConfig.class).partnerKey;

    @Override
    public String adaptCardType(CardType cardType) {
        return switch (cardType) {
            case VIETTEL -> "VIETTEL";
            case MOBIFONE -> "MOBIFONE";
            case VINAPHONE -> "VINAPHONE";
            case VIETNAMOBILE -> "VNMB";
            case GATE -> "GATE";
            case ZING -> "ZING";
            case GARENA -> "GARENA2";
            case VCOIN -> "VCOIN";
            default -> throw new IllegalArgumentException("Unsupported card type: " + cardType);
        };
    }

    @Override
    public PaymentStatus processPayment(Payment paymentarg) {
        CardDetail detail = (CardDetail) paymentarg.getDetail();
        List<Map<String, String>> formData = new ArrayList<>();

        String hash = HashUtil.md5(PARTNER_KEY + detail.pin + detail.serial);
        formData.add(Map.of(
                "telco", adaptCardType(detail.getType()),
                "code", detail.pin,
                "serial", detail.serial,
                "amount", String.valueOf((int) detail.getAmount()),
                "request_id", hash,
                "partner_id", PARTNER_ID,
                "sign", hash,
                "command", "charging"
        ));
        String response;
        try {
            response = postFormData(formData, GT1S_CREATE_URL).get();
        } catch (InterruptedException | ExecutionException e) {
            MessageUtil.debug("[GT1S-ProcessPayment] Error while processing payment: " + e.getMessage());
            return PaymentStatus.FAILED;
        }
        JsonObject jsonResponse = JsonParser.parseString(response).getAsJsonObject();
        if (jsonResponse.get("status").getAsInt() == 99) {
            MessageUtil.debug("[GT1S-ProcessPayment] " + jsonResponse);
            detail.setRefID(hash);
            paymentarg.getDetail().setRefID(hash);
            paymentarg.setDetail(detail);
            // call event to queue payment
            Bukkit.getPluginManager().callEvent(new PaymentQueueSuccessEvent(paymentarg));
            return PaymentStatus.PENDING;
        } else {
            MessageUtil.debug(response);
            return PaymentStatus.FAILED;
        }
    }

    @Override
    public PaymentResult getTransactionResult(PaymentDetail detail1) {
        CardDetail detail = (CardDetail) detail1;
        List<Map<String, String>> formData = new ArrayList<>();

        String hash = HashUtil.md5(PARTNER_KEY + detail.pin + detail.serial);
        formData.add(Map.of(
                "telco", adaptCardType(detail.getType()),
                "code", detail.pin,
                "serial", detail.serial,
                "amount", String.valueOf((int) detail.getAmount()),
                "request_id", hash,
                "partner_id", PARTNER_ID,
                "sign", hash,
                "command", "check"
        ));
        String response;
        try {
            response = postFormData(formData, GT1S_GET_STATUS_URL).get();
        } catch (InterruptedException | ExecutionException e) {
            MessageUtil.debug("[GT1S-GetTransactionResult] Error while getting transaction result: " + e.getMessage());
            return new PaymentResult(PaymentStatus.FAILED, (int) detail.getAmount(), "Error while processing request");
        }
        return getNencerAPIResult(detail, response);
    }

    @Override
    public PaymentStatus cancel(Payment payment) {
        throw new UnsupportedOperationException("Cannot cancel card payment");
    }

}
