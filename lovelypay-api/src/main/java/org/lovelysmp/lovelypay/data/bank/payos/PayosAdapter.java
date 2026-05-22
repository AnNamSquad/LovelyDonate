package org.lovelysmp.lovelypay.data.bank.payos;

import org.lovelysmp.lovelypay.data.PaymentStatus;

public class PayosAdapter {
    public static PaymentStatus getStatus(String statusCode) {
        return switch (statusCode) {
            case "PAID" -> PaymentStatus.SUCCESS;
            case "PENDING", "PROCESSING" -> PaymentStatus.PENDING;
            case "CANCELLED" -> PaymentStatus.CANCELLED;
            case "EXPIRED" -> PaymentStatus.EXPIRED;
            case null, default -> PaymentStatus.FAILED;
        };
    }
}
