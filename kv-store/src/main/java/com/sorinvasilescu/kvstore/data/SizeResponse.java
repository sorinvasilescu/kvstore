package com.sorinvasilescu.kvstore.data;

public class SizeResponse {

    private long size;

    public SizeResponse(long size) {
        this.size = size;
    }

    public long getSize() {
        return size;
    }
}
