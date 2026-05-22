package org.simpmc.simppay.handler.banking.payos.data;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class PayosResponse {
    @SerializedName("code")
    private String code;

    @SerializedName("desc")
    private String desc;

    @SerializedName("data")
    private PaymentData data;

    @SerializedName("signature")
    private String signature;

    // Getters and setters omitted for brevity

    @Data
    public static class PaymentData {
        @SerializedName("bin")
        private String bin;

        @SerializedName("accountNumber")
        private String accountNumber;

        @SerializedName("accountName")
        private String accountName;

        @SerializedName("amount")
        private double amount;

        @SerializedName("description")
        private String description;

        @SerializedName("orderCode")
        private int orderCode;

        @SerializedName("curency")
        private String curency;

        @SerializedName("paymentLinkId")
        private String paymentLinkId;

        @SerializedName("status")
        private String status;

        @SerializedName("checkoutUrl")
        private String checkoutUrl;

        @SerializedName("qrCode")
        private String qrCode;

        // Getters and setters omitted for brevity
    }
}
