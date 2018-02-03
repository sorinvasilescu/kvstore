package com.sorinvasilescu.kvstore.exceptions;

public class ItemNotFoundException extends Exception {
    private String key;

    public ItemNotFoundException(String message, String key) {
        super(message);
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
