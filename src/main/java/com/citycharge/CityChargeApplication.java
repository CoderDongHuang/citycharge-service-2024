package com.citycharge;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CityChargeApplication {
    public static void main(String[] args) {
        SpringApplication.run(CityChargeApplication.class, args);
    }
}