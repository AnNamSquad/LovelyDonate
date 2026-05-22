package org.simpmc.simppay.handler.banking.web2m.data;

import lombok.Getter;

@Getter
public class W2MTransactions {
    private String transactionID;
    private String amount;
    private String description;
    private String transactionDate;
    private String type;

    @Override
    public String toString() {
        return "Transaction{" +
                "transactionID=" + transactionID +
                ", amount=" + amount +
                ", description='" + description + '\'' +
                ", transactionDate='" + transactionDate + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}
