package com.pm.patientservice;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PatientServiceApplication {
    @Value("${server.port}")
    private int port;

    public static void main(String[] args) {
        SpringApplication.run(PatientServiceApplication.class, args);
    }

    @PostConstruct
    public void printSuccess() {
        System.out.println("Patient service is running on port: " + port);
    }
}
