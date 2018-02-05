package com.example.kvtest.endpoints;

import com.example.kvtest.ConfigStore;
import com.example.kvtest.requests.PutRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.util.Date;
import java.util.concurrent.*;

public class RequestRunner extends Thread {

    private final Logger log = LoggerFactory.getLogger("RequestRunner");

    private String baseUrl;
    private int totalRequests;
    private int rampUp;
    private Class requestClass;

    public RequestRunner(String baseUrl, Class requestClass) {
        this.baseUrl = baseUrl;
        this.rampUp = ConfigStore.rampUp;
        this.requestClass = requestClass;
        try {
            this.totalRequests = requestClass.getMethod("getTotal")();
    }

    public void run() {
        // create executor
        BlockingQueue<Runnable> queue = new LinkedBlockingQueue<>();
        ThreadPoolExecutor executor = new ThreadPoolExecutor(20, 20, 0, TimeUnit.SECONDS, queue);

        // initialize values for ramp-up
        int target;
        if (rampUp > 0) {
            target = 0;
        } else {
            target = totalRequests;
        }

        // request numbers
        int totalRequestsDone = 0;
        int requestsThisSecond = 0;

        // get start date for ramp-up purposes
        Date startDate = new Date();
        long spent = 0;

        // get constructor for later
        Constructor<?> constructor = requestClass.getConstructors()[0];

        while (totalRequestsDone < 100) {

            // send requests to queue if not over target
            if (requestsThisSecond < target) {
                for (int i=0; i < (target-requestsThisSecond); i++) {
                    // instantiate the correct request class
                    Runnable request;
                    try {
                        request = (Runnable) constructor.newInstance(baseUrl);
                    } catch (Exception e) {
                        log.error("Could not instantiate request class: " + requestClass.getName());
                        return;
                    }

                    executor.execute(request);
                    requestsThisSecond++;
                    totalRequestsDone++;
                }
            }

            // check time spent, if still ramping up
            if (target < totalRequests) {
                Date now = new Date();
                long lastSpent = spent;
                spent = (now.getTime() - startDate.getTime())/1000;
                // but only once per second
                if (lastSpent < spent) {
                    requestsThisSecond = 0;
                    if (spent < rampUp) {
                        target = Math.round(totalRequests * spent / rampUp);
                    } else {
                        target = totalRequests;
                    }
                }
            }

            try {
                sleep(50);
            } catch (InterruptedException e) {
                log.warn("Thread sleep interrupted: " + this.getId());
            }

        }

        executor.shutdown();
    }
}
