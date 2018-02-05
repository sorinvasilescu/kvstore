package com.example.kvtest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class PerformanceTester implements Runnable {

    @Value("${apps.baseUrl}")
    private String[] baseUrl;

    @Value("${requests.put.total}")
    private int requestPutTotal;

    @Value("${requests.get.total}")
    private int requestsGetTotal;

    @Value("${requests.delete.total}")
    private int requestsDeleteTotal;

    @Value("${requests.size.total}")
    private int requestsSizeTotal;

    @Value("${requests.rampuptime}")
    private int rampUp;

    @Value("${payload.size}")
    private int payloadSize;

    private final Logger log = LoggerFactory.getLogger("PerformanceTester");

    public PerformanceTester() {
        log.info("Performance tester created");
    }

    public void run() {
        log.info("Performance tester running");

        ConfigStore.payloadSize = this.payloadSize;
        ConfigStore.rampUp = this.rampUp;
        ConfigStore.requestPutTotal = requestPutTotal;
        ConfigStore.requestsGetTotal = requestsGetTotal;
        ConfigStore.requestsDeleteTotal = requestsDeleteTotal;
        ConfigStore.requestsSizeTotal = requestsSizeTotal;

        if (baseUrl.length < 1) {
            log.error("At least one baseURL is necessary");
            return;
        }

        for (int i=0; i < baseUrl.length; i++) {
            new InstanceTester(baseUrl[i]);
        }
    }
}
