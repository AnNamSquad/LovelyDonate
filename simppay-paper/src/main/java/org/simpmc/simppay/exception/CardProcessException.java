package org.simpmc.simppay.exception;

public class CardProcessException extends RuntimeException {
    public CardProcessException(String message) {
        super("Error happened when trying to fetch card returned data while sending" + message);
    }
}
