package org.simpmc.lovelypay.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.simpmc.lovelypay.data.PaymentStatus;

@Data
@AllArgsConstructor
public class PaymentResult {
    public PaymentStatus status;
    public int amount;
    public String message;
}
