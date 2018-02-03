package com.sorinvasilescu.kvstore.application;

import com.sorinvasilescu.kvstore.service.FileStorage;
import com.sorinvasilescu.kvstore.service.InMemoryStorage;
import com.sorinvasilescu.kvstore.service.StorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfiguration {

    @Autowired
    ConfigurableApplicationContext context;

    @Value("${kvstore.storagetype}")
    String storageType;

    private final Logger log = LoggerFactory.getLogger("AppConfiguration");

    @Bean
    public StorageService getService() {
        if (storageType.equals("memory")) {
            log.info("Storage type: in memory");
            return new InMemoryStorage();
        } else {
            if (storageType.equals("file")) {
                log.info("Storage type: filesystem");
                return new FileStorage();
            }
        }

        log.error("Storage type not found. Please configure storage type!");
        SpringApplication.exit(context);
        return null;
    }
}
