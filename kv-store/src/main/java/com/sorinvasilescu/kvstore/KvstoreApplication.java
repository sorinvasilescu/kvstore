package com.sorinvasilescu.kvstore;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@EnableSwagger2
public class KvstoreApplication {

	public static void main(String[] args) {
	    SpringApplication.run(KvstoreApplication.class, args);
    }

    // swagger config
    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2).select()
                .apis(RequestHandlerSelectors.basePackage("com.sorinvasilescu.kvstore.controller")).paths(PathSelectors.any()).build()
                .pathMapping("/").useDefaultResponseMessages(false);
    }
}
