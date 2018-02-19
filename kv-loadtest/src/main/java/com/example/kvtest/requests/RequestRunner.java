package com.example.kvtest.requests;

import com.example.kvtest.statics.ConfigStore;
import com.example.kvtest.statics.StatsStore;
import com.sun.jmx.remote.internal.ArrayQueue;
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

        int poolsize = 150;

        // create executor
        ThreadPoolExecutor executor = new ThreadPoolExecutor(poolsize, poolsize, 0, TimeUnit.SECONDS, new LinkedBlockingQueue<>());
        executor.prestartAllCoreThreads();

        List<Runnable> taskList = new ArrayList<>();

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

        while (totalRequestsDone < 20000) {

            // send requests to queue if not over target
            if ((executor.getActiveCount() < poolsize) && (requestsThisSecond < target)) {
                int i = 0;
                while (i < Math.min((target-requestsThisSecond), poolsize-executor.getActiveCount())) {
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
                    i++;
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
            log.info("ThreadPool waiting for termination");
            executor.awaitTermination(5, TimeUnit.SECONDS);
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
        Double top95 = responseTimes.subList(0,(int)Math.round(0.95*responseTimes.size())).stream().mapToInt(i -> i.intValue()).average().getAsDouble();
        Double top99 = responseTimes.subList(0,(int)Math.round(0.99*responseTimes.size())).stream().mapToInt(i -> i.intValue()).average().getAsDouble();
        Double top75 = responseTimes.subList(0,(int)Math.round(0.75*responseTimes.size())).stream().mapToInt(i -> i.intValue()).average().getAsDouble();
        Double top50 = responseTimes.subList(0,(int)Math.round(0.50*responseTimes.size())).stream().mapToInt(i -> i.intValue()).average().getAsDouble();

        String result = "";

        result += "\n__________________________________________\n";
        result += "Results for " + baseUrl + " " + requestClass.getName() + "\n";
        result += "Executed " + executed + " " + requestClass.getName() + " requests out of which " + successful + " successful (" + Math.round(10000*successful/executed)/100 + " %)\n";
        result += "Average response time: " + Math.round(average) + " ms\n";
        result += "Top 50% requests' response time: " + Math.round(top50) + " ms\n";
        result += "Top 75% requests' response time: " + Math.round(top75) + " ms\n";
        result += "Top 95% requests' response time: " + Math.round(top95) + " ms\n";
        result += "Top 99% requests' response time: " + Math.round(top99) + " ms\n";
        result += "‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾\n";
        log.info(result);

        log.info("Request runner finished");
    }
}
