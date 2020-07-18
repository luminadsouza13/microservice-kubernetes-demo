package com.examples;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
public class Application {
    public static void main(String[] args) {

        SpringApplication app = new SpringApplication(Application.class);
        app.setAdditionalProfiles("Magic");
        app.run(args);

    }
}