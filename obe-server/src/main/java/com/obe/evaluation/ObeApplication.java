package com.obe.evaluation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ObeApplication {
    public static void main(String[] args) {
        SpringApplication.run(ObeApplication.class, args);
    }
}
