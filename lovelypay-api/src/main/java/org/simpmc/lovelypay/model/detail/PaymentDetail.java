package org.simpmc.lovelypay.model.detail;

public interface PaymentDetail {
    double getAmount();

    PaymentDetail setAmount(int amount);

    String getRefID();

    void setRefID(String refID);

    String getDescription();

    String getQRCode();
}
