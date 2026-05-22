package org.lovelysmp.lovelypay.database.dto;

import lombok.Builder;
import lombok.Data;
import org.lovelysmp.lovelypay.data.PaymentType;
import org.lovelysmp.lovelypay.data.card.CardType;
import org.lovelysmp.lovelypay.database.entities.BankingPayment;
import org.lovelysmp.lovelypay.database.entities.CardPayment;

import java.time.Instant;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Data
@Builder
public class PaymentRecord {
    private UUID paymentId;
    private Date timestamp;
    private String playerName; // optional
    private PaymentType paymentType;      // e.g. "Bank" or "Card"
    private String provider;      // bank API name or card type
    private Optional<String> serial;
    private Optional<String> pin;
    private double amount;        // face‐value amount
    private Optional<Double> trueAmount; // only for cards
    private Optional<CardType> telco;
    private String refId;

    public static PaymentRecord fromBank(BankingPayment bp) {
        return PaymentRecord.builder()
                .paymentId(bp.getPaymentID())
                .timestamp(Date.from(Instant.ofEpochSecond(bp.getTimestamp()))) // TODO: better way to handle this
                .playerName(bp.getPlayer().getName())
                .paymentType(PaymentType.BANKING)
                .provider(bp.getApiProvider().name())
                .serial(Optional.empty())
                .pin(Optional.empty())
                .amount(bp.getAmount())
                .trueAmount(Optional.empty())
                .telco(Optional.empty())
                .refId(bp.getRefID())
                .build();
    }

    public static PaymentRecord fromCard(CardPayment cp) {
        return PaymentRecord.builder()
                .paymentId(cp.getPaymentID())
                .timestamp(Date.from(Instant.ofEpochSecond(cp.getTimestamp()))) // TODO: better way to handle this
                .paymentType(PaymentType.CARD)
                .playerName(cp.getPlayer().getName())
                .provider(cp.getApiProvider().name())
                .serial(Optional.of(cp.getSerial()))
                .pin(Optional.of(cp.getPin()))
                .amount(cp.getAmount())
                .trueAmount(Optional.of(cp.getTrueAmount()))
                .refId(cp.getRefID())
                .telco(Optional.of(cp.getCardType()))
                .build();
    }

    public String getTelco() {
        if (telco.isEmpty()) {
            return "?";
        }
        return telco.map(CardType::toString).orElse("?");
    }
}
