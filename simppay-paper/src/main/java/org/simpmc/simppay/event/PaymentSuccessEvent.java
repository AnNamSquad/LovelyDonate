package org.simpmc.simppay.event;

import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.simpmc.simppay.data.PaymentType;
import org.simpmc.simppay.model.Payment;
import org.simpmc.simppay.model.detail.CardDetail;
import org.simpmc.simppay.model.detail.PaymentDetail;

import java.util.UUID;

@Getter
public class PaymentSuccessEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private final UUID paymentID;
    private final UUID playerUUID;
    private final double amount;
    private final PaymentType paymentType;
    private final PaymentDetail paymentDetail;
    private final Payment payment;
    private final boolean wrongPrice;
    private final double trueAmount;

    public PaymentSuccessEvent(Payment payment) {
        this.paymentID = payment.getPaymentID();
        this.playerUUID = payment.getPlayerUUID();
        this.amount = payment.getDetail().getAmount();
        this.paymentType = payment.getPaymentType();
        this.paymentDetail = payment.getDetail();
        this.payment = payment;
        this.wrongPrice = false;
        this.trueAmount = paymentType.equals(PaymentType.BANKING) ? amount : ((CardDetail) payment.getDetail()).getTrueAmount();
    }

    public PaymentSuccessEvent(Payment payment, boolean wrongPrice) {
        this.paymentID = payment.getPaymentID();
        this.playerUUID = payment.getPlayerUUID();
        this.amount = payment.getDetail().getAmount();
        this.paymentType = payment.getPaymentType();
        this.paymentDetail = payment.getDetail();
        this.payment = payment;
        this.wrongPrice = wrongPrice;
        this.trueAmount = paymentType.equals(PaymentType.BANKING) ? amount : ((CardDetail) payment.getDetail()).getTrueAmount();
    }


    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }
}
