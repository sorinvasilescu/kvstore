package com.example.kvtest.requests;

import com.example.kvtest.data.Item;
import com.example.kvtest.statics.ConfigStore;
import com.example.kvtest.statics.StatsStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Date;

public class GetRequest extends WriteDependentRequest {

    private final Logger log = LoggerFactory.getLogger("GetRequest");

    public GetRequest(String baseUrl) {
        super(baseUrl);
    }

    @Override
    public void run() {

        Item reference = this.waitForItem();

        RestTemplate rest = new RestTemplate();
        Date before = new Date();
        Date after;
        ResponseEntity<Item> response = rest.exchange(baseUrl + "/api/" + reference.getKey(), HttpMethod.GET, null, Item.class);
        Item item = response.getBody();
        if (response.getStatusCode().equals(HttpStatus.OK) && item.equals(reference)) {
            after = new Date();
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
