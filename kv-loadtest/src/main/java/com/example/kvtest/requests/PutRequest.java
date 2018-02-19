package com.example.kvtest.requests;

import com.example.kvtest.statics.ConfigStore;
import com.example.kvtest.data.Item;
import com.example.kvtest.statics.KeyStore;
import com.example.kvtest.statics.StatsStore;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
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
        Date after;
        ResponseEntity<?> response = rest.exchange(baseUrl + "/api", HttpMethod.PUT, entity, Void.class);
        after = new Date();

        if (response.getStatusCode().equals(HttpStatus.OK)) {
            long elapsed = after.getTime() - before.getTime();
            synchronized (StatsStore.responseTimes) {
                StatsStore.responseTimes.add(PutRequest.class, elapsed);
            }
            synchronized (StatsStore.successfulRequestCount) {
                StatsStore.successfulRequestCount.compute(PutRequest.class, (k, v) -> v + 1);
            }
            synchronized (KeyStore.keys) {
                if (!KeyStore.keys.containsKey(baseUrl)) {
                    KeyStore.keys.put(baseUrl,new HashMap<>());
                }
            }
            synchronized (KeyStore.keys.get(baseUrl)) {
                KeyStore.keys.get(baseUrl).put(key,value);
            }
            //log.info(response.getStatusCode().toString() + " in " + elapsed + " ms");
        } else {
            synchronized (StatsStore.failedRequestCount) {
                StatsStore.failedRequestCount.compute(PutRequest.class, (k, v) -> v + 1);
            }
            //log.info(response.getStatusCode().toString());
        }
    }

    public static int getTotal() {
        return ConfigStore.requestsPutTotal;
    }
}
