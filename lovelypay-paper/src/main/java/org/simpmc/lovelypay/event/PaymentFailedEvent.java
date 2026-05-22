package org.simpmc.lovelypay.event;

import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.simpmc.lovelypay.data.PaymentType;
import org.simpmc.lovelypay.model.Payment;
import org.simpmc.lovelypay.model.detail.PaymentDetail;

import java.util.UUID;

@Getter
public class PaymentFailedEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private final UUID paymentID;
    private final UUID playerUUID;
    private final double amount;
    private final PaymentType paymentType;
    private final PaymentDetail paymentDetail;
    private final Payment payment;

    public PaymentFailedEvent(Payment payment) {
        this.paymentID = payment.getPaymentID();
        this.playerUUID = payment.getPlayerUUID();
        this.amount = payment.getDetail().getAmount();
        this.paymentType = payment.getPaymentType();
        this.paymentDetail = payment.getDetail();
        this.payment = payment;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }
}
