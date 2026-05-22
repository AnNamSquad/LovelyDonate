package org.lovelysmp.lovelypay.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.lovelysmp.lovelypay.data.PaymentStatus;

@Data
@AllArgsConstructor
public class PaymentResult {
    public PaymentStatus status;
    public int amount;
    public String message;
}
