package com.example.kvtest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class KvTestApplication {

	public static void main(String[] args) {
		ConfigurableApplicationContext context = SpringApplication.run(KvTestApplication.class, args);
		context.getBean(PerformanceTester.class).run();
	}
}
