package com.example.kvtest.requests;

import com.example.kvtest.data.Item;
import com.example.kvtest.statics.ConfigStore;
import com.example.kvtest.statics.KeyStore;
import com.example.kvtest.statics.StatsStore;
import org.apache.commons.lang3.RandomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Date;

public class GetRequest extends Thread {

    private final Logger log = LoggerFactory.getLogger("GetRequest");

    private String baseUrl;

    public GetRequest(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    @Override
    public void run() {

        int size = 0;

        // wait for at least one item to be written
        while (size < 1) {
            // wait
            try {
                sleep(100);
            } catch (InterruptedException e) {
                log.warn("Get thread sleep interrupted");
            }
            // get written key array size
            synchronized (KeyStore.keys) {
                size = KeyStore.keys.size();
            }
        }

        int index = RandomUtils.nextInt(0,size-1);
        String key;
        byte[] value;

        synchronized (KeyStore.keys) {
            key = (String)KeyStore.keys.keySet().toArray()[index];
            value = KeyStore.keys.get(key);
        }

        RestTemplate rest = new RestTemplate();
        Date before = new Date();
        ResponseEntity<Item> response = rest.exchange(baseUrl + "/api/" + key, HttpMethod.GET, null, Item.class);
        Date after = new Date();
        Item item = response.getBody();
        if (response.getStatusCode().equals(HttpStatus.OK) && item.equals(new Item(key,value))) {
            long elapsed = after.getTime() - before.getTime();
            synchronized (StatsStore.responseTimes) {
                StatsStore.responseTimes.add(GetRequest.class, elapsed);
            }
            synchronized (StatsStore.successfulRequestCount) {
                StatsStore.successfulRequestCount.compute(GetRequest.class, (k, v) -> v + 1);
            }
            //log.info(response.getStatusCode().toString() + " in " + elapsed + " ms");
        } else {
            synchronized (StatsStore.failedRequestCount) {
                StatsStore.failedRequestCount.compute(GetRequest.class, (k, v) -> v + 1);
            }
            //log.info(response.getStatusCode().toString());
        }
    }

    public static int getTotal() {
        return ConfigStore.requestsGetTotal;
    }
}
