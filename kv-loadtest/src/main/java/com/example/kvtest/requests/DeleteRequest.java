package com.example.kvtest.requests;

import com.example.kvtest.data.Item;
import com.example.kvtest.statics.ConfigStore;
import com.example.kvtest.statics.KeyStore;
import com.example.kvtest.statics.StatsStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Date;

public class DeleteRequest extends WriteDependentRequest {

    private final Logger log = LoggerFactory.getLogger("DeleteRequest");

    public DeleteRequest(String baseUrl) {
        super(baseUrl);
    }

    @Override
    public void run() {
        Item reference = this.waitForItem(true);

        RestTemplate rest = new RestTemplate();
        Date before = new Date();
        Date after;
        ResponseEntity<?> response = rest.exchange(baseUrl + "/api/" + reference.getKey(), HttpMethod.DELETE, null, Void.class);
        if (response.getStatusCode().equals(HttpStatus.OK)) {
            after = new Date();
            long elapsed = after.getTime() - before.getTime();
            synchronized (StatsStore.responseTimes) {
                StatsStore.responseTimes.add(DeleteRequest.class, elapsed);
            }
            synchronized (StatsStore.successfulRequestCount) {
                StatsStore.successfulRequestCount.compute(DeleteRequest.class, (k, v) -> v + 1);
            }
            synchronized (KeyStore.keys.get(baseUrl)) {
                KeyStore.keys.get(baseUrl).remove(reference.getKey());
            }
            //log.info(response.getStatusCode().toString() + " in " + elapsed + " ms");
        } else {
            synchronized (StatsStore.failedRequestCount) {
                StatsStore.failedRequestCount.compute(DeleteRequest.class, (k, v) -> v + 1);
            }
            //log.info(response.getStatusCode().toString());
        }

    }

    public static int getTotal() {
        return ConfigStore.requestsDeleteTotal;
    }

}
