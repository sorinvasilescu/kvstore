package com.sorinvasilescu.kvstore.model;

public class Item {

    private String key;
    private String value;

    public Item(String key, String value) {
        this.key = key;
        this.value = value;
    }

    // getters serve the autoconversion process from class to json
    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }
}
