package com.example.kvtest;

import com.example.kvtest.requests.GetRequest;
import com.example.kvtest.requests.RequestRunner;
import com.example.kvtest.requests.PutRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class InstanceTester {

    private String baseUrl;

    private final Logger log = LoggerFactory.getLogger("PerformanceTester");

    public InstanceTester(String baseUrl) {
        this.baseUrl = baseUrl;
        log.info("Performance tester for " + baseUrl + " has been created");
        runTest();
    }

    private void runTest() {
        try {
            new RequestRunner(baseUrl, PutRequest.class).run();
            new RequestRunner(baseUrl, GetRequest.class).run();
        } catch (Exception e) {
            log.error("Exception running tests: " + e);
            e.printStackTrace();
        }
    }
}
