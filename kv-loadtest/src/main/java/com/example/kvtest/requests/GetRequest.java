package com.example.kvtest.requests;

import com.example.kvtest.data.Item;
import com.example.kvtest.statics.ConfigStore;
import com.example.kvtest.statics.StatsStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.util.Date;
import java.util.List;

public class GetRequest extends WriteDependentRequest {

    private final Logger log = LoggerFactory.getLogger("GetRequest");

    public GetRequest(String baseUrl) {
        super(baseUrl);
    }

    @Override
    public void run() {

        Item reference = this.waitForItem();

        RestTemplate rest = new RestTemplate();
        ( (SimpleClientHttpRequestFactory) rest.getRequestFactory()).setOutputStreaming(true);
        // set response handler that does nothing, as we treat errors below
        rest.setErrorHandler(WriteDependentRequest.errorHandler);
        Date after;
        Date before = new Date();
        ResponseEntity<Item> response = rest.exchange(baseUrl + "/api/" + reference.getKey(), HttpMethod.GET, null, Item.class);
        after = new Date();
        if (response.getStatusCode().equals(HttpStatus.OK) && response.getBody().equals(reference)) {
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
