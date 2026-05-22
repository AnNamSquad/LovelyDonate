package org.lovelysmp.lovelypay.handler;

import org.lovelysmp.lovelypay.data.PaymentStatus;
import org.lovelysmp.lovelypay.model.Payment;
import org.lovelysmp.lovelypay.model.PaymentResult;
import org.lovelysmp.lovelypay.model.detail.PaymentDetail;

public interface PaymentHandler {

    PaymentStatus processPayment(Payment payment); // should only return pending or exist

    PaymentResult getTransactionResult(PaymentDetail detail);

    PaymentStatus cancel(Payment payment);
}
