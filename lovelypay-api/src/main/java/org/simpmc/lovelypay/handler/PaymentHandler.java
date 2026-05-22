package org.simpmc.lovelypay.handler;

import org.simpmc.lovelypay.data.PaymentStatus;
import org.simpmc.lovelypay.model.Payment;
import org.simpmc.lovelypay.model.PaymentResult;
import org.simpmc.lovelypay.model.detail.PaymentDetail;

public interface PaymentHandler {

    PaymentStatus processPayment(Payment payment); // should only return pending or exist

    PaymentResult getTransactionResult(PaymentDetail detail);

    PaymentStatus cancel(Payment payment);
}
