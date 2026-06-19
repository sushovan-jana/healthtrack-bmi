package com.healthtrack.bmi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing // Enables Spring Data JPA Auditing for createdAt and updatedAt
public class HealthTrackBmiApplication {

    public static void main(String[] args) {
        SpringApplication.run(HealthTrackBmiApplication.class, args);
    }
}
