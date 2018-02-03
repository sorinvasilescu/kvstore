package com.sorinvasilescu.kvstore.exceptions;

public class DuplicateItemException extends Exception {
    private String key;

    public DuplicateItemException(String message, String key) {
        super(message);
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
