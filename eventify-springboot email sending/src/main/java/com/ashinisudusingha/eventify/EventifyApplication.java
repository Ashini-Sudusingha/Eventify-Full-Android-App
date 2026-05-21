package com.ashinisudusingha.eventify;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class EventifyApplication {

    public static void main(String[] args) {
        SpringApplication.run(EventifyApplication.class, args);
        System.out.println("✅ Email Verification Service Started!");
    }

}
