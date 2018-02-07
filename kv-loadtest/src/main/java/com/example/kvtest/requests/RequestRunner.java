package com.example.kvtest.requests;

import com.example.kvtest.statics.ConfigStore;
import com.example.kvtest.statics.StatsStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

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
            this.totalRequests = (int)requestClass.getMethod("getTotal").invoke(null);
        } catch (Exception e) {
            log.error("Cannot invoke getTotal method on class " + requestClass.getName());
        }
    }

    public void run() {
        // create executor
        ThreadPoolExecutor executor = new ThreadPoolExecutor(75, 75, 0, TimeUnit.SECONDS, new LinkedBlockingQueue<>());

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
        long lastSpent = 0;

        // get constructor for later
        Constructor<?> constructor = requestClass.getConstructors()[0];
        executor.prestartAllCoreThreads();

        while (totalRequestsDone < 5000) {

            // send requests to queue if not over target
            if ((executor.getActiveCount() < 75) && (requestsThisSecond < target)) {
                for (int i=0; i < Math.min((target-requestsThisSecond), 75-executor.getActiveCount())+1; i++) {
                    // instantiate the correct request class
                    Runnable request;
                    try {
                        request = (Runnable) constructor.newInstance(baseUrl);
                    } catch (Exception e) {
                        log.error("Could not instantiate request class: " + requestClass.getName());
                        return;
                    }

                    requestsThisSecond++;
                    totalRequestsDone++;
                    executor.execute(request);
                }
            }

            // check time spent, if still ramping up
            Date now = new Date();
            spent = (now.getTime() - startDate.getTime()) / 1000;
            // but only once per second
            if (lastSpent < spent) {
                log.info("Elapsed: " + spent + " | target " + target + " | completed " + requestsThisSecond);
                lastSpent = spent;
                requestsThisSecond = 0;
                if (target < totalRequests) {
                    if (spent < rampUp) {
                        target = Math.round(totalRequests * spent / rampUp);
                    } else {
                        target = totalRequests;
                    }
                }
            }

            try {
                sleep(5);
            } catch (InterruptedException e) {
                log.warn("Thread sleep interrupted: " + this.getId());
            }

        }

        try {
            executor.awaitTermination(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            log.warn("Termination wait interrupted");
        }

        Long successful;
        synchronized (StatsStore.successfulRequestCount) {
            successful = StatsStore.successfulRequestCount.get(requestClass);
        }

        Long failed;
        synchronized (StatsStore.failedRequestCount) {
            failed = StatsStore.failedRequestCount.get(requestClass);
        }

        Long executed = successful + failed;
        List<Long> responseTimes;

        synchronized (StatsStore.responseTimes) {
            responseTimes = new ArrayList<>(StatsStore.responseTimes.get(requestClass));
        }

        Double average = responseTimes.stream().collect(Collectors.averagingLong(Long::longValue));
        responseTimes.sort(Long::compareTo);
        Double top = responseTimes.subList(0,(int)Math.round(0.95*responseTimes.size())).stream().mapToInt(i -> i.intValue()).average().getAsDouble();

        log.info("__________________________________________");
        log.info("Executed " + executed + " " + requestClass.getName() + " requests out of which " + successful + " successful (" + Math.round(10000*successful/executed)/100 + " %)");
        log.info("Average response time: " + Math.round(average) + " ms");
        log.info("Top 95% requests' response time: " + Math.round(top) + " ms");
        log.info("‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾");
    }
}
