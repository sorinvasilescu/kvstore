package com.example.kvtest.statics;

import com.example.kvtest.requests.DeleteRequest;
import com.example.kvtest.requests.GetRequest;
import com.example.kvtest.requests.PutRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.HashMap;
import java.util.Map;

@Component
public class StatsStore {
    public static final MultiValueMap<Class, Long> responseTimes = new LinkedMultiValueMap<>();
    public static final Map<Class, Long> successfulRequestCount = new HashMap<>();
    public static final Map<Class, Long> failedRequestCount = new HashMap<>();

    public StatsStore() {
        successfulRequestCount.put(PutRequest.class, 0L);
        failedRequestCount.put(PutRequest.class, 0L);
        successfulRequestCount.put(GetRequest.class, 0L);
        failedRequestCount.put(GetRequest.class, 0L);
        successfulRequestCount.put(DeleteRequest.class, 0L);
        failedRequestCount.put(DeleteRequest.class, 0L);
    }
}
