package com.example.buggyapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// DEFECT: Missing proper security configuration
@SpringBootApplication
public class BuggyApplication {

    // DEFECT: Empty catch block and generic exception
    public static void main(String[] args) {
        try {
            SpringApplication.run(BuggyApplication.class, args);
        } catch (Exception e) {
            // DEFECT: Swallowing exceptions without logging
        }
    }
}
