package org.simpmc.simppay.handler.banking.web2m.data;

import lombok.Getter;

import java.util.List;


public class W2MReponse {
    private boolean status;
    @Getter
    private String message;
    @Getter
    private List<W2MTransactions> transactions;

    @Override
    public String toString() {
        return "TransactionResponse{" +
                "status=" + status +
                ", message='" + message + '\'' +
                ", transactions=" + transactions +
                '}';

    }

    public boolean getStatus() {
        return status;
    }
}
