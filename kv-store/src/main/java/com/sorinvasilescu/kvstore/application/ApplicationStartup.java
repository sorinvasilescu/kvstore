package com.sorinvasilescu.kvstore.application;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
public class ApplicationStartup implements ApplicationListener<ApplicationReadyEvent> {

    @Autowired
    ConfigurableApplicationContext context;

    @Value("${kvstore.storagetype}")
    String storageType;

    @Value("${kvstore.location}")
    private String location;

    private final Logger log = LoggerFactory.getLogger("ApplicationStartup");

    @Override
    public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {
        if (!storageType.equals("file")) {
            log.info("Will not setup filesystem");
            return;
        }

        if (location != null) {
            log.info("Will write files to: " + location);
            Path baseDir = Paths.get(location);
            if (!Files.isDirectory(baseDir)) {
                log.error("Location is not a folder!");
            } else if (!Files.isWritable(baseDir)) {
                log.error("Location is not writeable!");
            } else {
                return;
            }
        } else {
            log.error("Storage is filesystem, but location has not been configured. Please configure location!");
        }
        SpringApplication.exit(context);
    }
}
