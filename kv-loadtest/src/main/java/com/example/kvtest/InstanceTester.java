package com.example.kvtest;

import com.example.kvtest.requests.DeleteRequest;
import com.example.kvtest.requests.GetRequest;
import com.example.kvtest.requests.RequestRunner;
import com.example.kvtest.requests.PutRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class InstanceTester extends Thread {

    private String baseUrl;

    private final Logger log = LoggerFactory.getLogger("InstanceTester");

    public InstanceTester(String baseUrl) {
        this.baseUrl = baseUrl;
        log.info("Performance tester for " + baseUrl + " has been created");
    }

    @Override
    public void run() {
        try {
            new RequestRunner(baseUrl, PutRequest.class).run();
            new RequestRunner(baseUrl, GetRequest.class).run();
            new RequestRunner(baseUrl, DeleteRequest.class).run();
        } catch (Exception e) {
            log.error("Exception running tests: " + e);
            e.printStackTrace();
        }

        log.info("Instance tester finished");
    }
}
