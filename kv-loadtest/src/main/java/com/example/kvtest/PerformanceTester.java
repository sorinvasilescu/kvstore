package com.example.kvtest;

import com.example.kvtest.statics.ConfigStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class PerformanceTester extends Thread {

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

    @Autowired
    ConfigurableApplicationContext context;

    private final Logger log = LoggerFactory.getLogger("PerformanceTester");

    public PerformanceTester() {
        log.info("Performance tester created");
    }

    public void run() {
        log.info("Performance tester running");

        ConfigStore.payloadSize = this.payloadSize;
        ConfigStore.rampUp = this.rampUp;
        ConfigStore.requestsPutTotal = this.requestPutTotal;
        ConfigStore.requestsGetTotal = this.requestsGetTotal;
        ConfigStore.requestsDeleteTotal = this.requestsDeleteTotal;
        ConfigStore.requestsSizeTotal = this.requestsSizeTotal;

        if (baseUrl.length < 1) {
            log.error("At least one baseURL is necessary");
            return;
        }

        List<InstanceTester> testers = new ArrayList<>();

        for (int i=0; i < baseUrl.length; i++) {
            InstanceTester tester = new InstanceTester(baseUrl[i]);
            tester.start();
            testers.add(tester);
        }

        boolean running = true;
        while(running) {
            running = false;
            try {
                sleep(500);
            } catch (InterruptedException e) {
                log.warn("Sleep interrupted");
            }
            for (InstanceTester tester : testers) {
                if (tester.isAlive()) running = true;
            }
        }

        log.info("Performance tester finished");
        context.close();
    }
}
