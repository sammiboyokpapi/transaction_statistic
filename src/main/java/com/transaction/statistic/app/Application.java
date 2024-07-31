package com.transaction.statistic.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point for the Spring Boot application.
 * 
 * This class contains the main method which is the starting point of the application.
 * The @SpringBootApplication annotation enables Spring Boot's auto-configuration, 
 * component scanning, and configuration properties.
 * 
 * Author: [ Okpapi Samuel ]
 */
@SpringBootApplication
public class Application {

    /**
     * Main method to run the Spring Boot application.
     * 
     * This method uses SpringApplication.run() to launch the application. 
     * It sets up the Spring application context and performs all necessary initialization.
     * 
     * @param args Command-line arguments passed to the application.
     */
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
