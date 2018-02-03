package com.sorinvasilescu.kvstore.exceptions;

import java.io.IOException;

public class ItemWriteFailedException extends Exception {
    private String key;
    private IOException cause;

    public ItemWriteFailedException(String message, String key, IOException cause) {
        super(message);
        this.key = key;
        this.cause = cause;
    }

    public String getKey() {
        return key;
    }

    @Override
    public IOException getCause() {
        return cause;
    }
}
