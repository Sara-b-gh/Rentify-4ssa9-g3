package com.rentify.contrats;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class ContratsServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ContratsServiceApplication.class, args);
    }
}
