package org.simpmc.simppay.handler.banking.payos.data;

import com.google.gson.annotations.SerializedName;
import lombok.Builder;

@Builder
public class PayosPayment {
    @SerializedName("orderCode")
    private int orderCode;

    @SerializedName("amount")
    private double amount;

    @SerializedName("description")
    private String description;

    @SerializedName("cancelUrl")
    private String cancelUrl;

    @SerializedName("returnUrl")
    private String returnUrl;

    @SerializedName("expiredAt")
    private long expiredAt; // 5 mins

    @SerializedName("signature")
    private String signature;
}
