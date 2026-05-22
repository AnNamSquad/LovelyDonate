package org.simpmc.lovelypay.model;

import lombok.Data;
import lombok.experimental.Accessors;
import org.simpmc.lovelypay.data.PaymentStatus;
import org.simpmc.lovelypay.data.PaymentType;
import org.simpmc.lovelypay.model.detail.CardDetail;
import org.simpmc.lovelypay.model.detail.PaymentDetail;

import java.util.Date;
import java.util.UUID;

@Accessors(chain = true)
@Data
public class Payment {

    private final UUID paymentID; // Internal ID of the plugin
    private final UUID playerUUID;
    private final PaymentType paymentType;
    private PaymentDetail detail;
    private PaymentStatus status;
    private Date createdAt; // The time when the payment was created


    public Payment(UUID paymentID, UUID playerUUID, PaymentDetail detail) {
        this.paymentID = paymentID;
        this.playerUUID = playerUUID;
        this.detail = detail;
        this.createdAt = new Date();
        if (detail instanceof CardDetail) {
            this.paymentType = PaymentType.CARD;
        } else {
            this.paymentType = PaymentType.BANKING;
        }
        this.status = null;
    }


}
