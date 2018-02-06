package com.example.kvtest.requests;

import com.example.kvtest.statics.ConfigStore;
import com.example.kvtest.data.Item;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Date;
import java.util.Random;

public class PutRequest implements Runnable {

    private final Logger log = LoggerFactory.getLogger("PutRequest");

    private String baseUrl;
    private int payloadSize;

    public PutRequest(String baseUrl) {
        this.baseUrl = baseUrl;
        this.payloadSize = ConfigStore.payloadSize;
    }

    @Override
    public void run() {
        RestTemplate rest = new RestTemplate();

        Random rand = new Random();
        int keyLength = RandomUtils.nextInt(4,65);
        byte[] value = new byte[payloadSize];
        rand.nextBytes(value);
        String key = RandomStringUtils.randomAlphanumeric(keyLength);
        Item item = new Item(key,value);

        HttpEntity<?> entity = new HttpEntity<>(item);

        Date before = new Date();
        ResponseEntity<?> response = rest.exchange(baseUrl, HttpMethod.PUT, entity, Void.class);
        Date after = new Date();
        long elapsed = after.getTime() - before.getTime();

        log.info(response.getStatusCode().toString() + " in " + elapsed + " ms");
    }

    public static int getTotal() {
        return ConfigStore.requestPutTotal;
    }
}
