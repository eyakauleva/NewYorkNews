package com.solvd.micro9.scooters;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafkaStreams;

@SpringBootApplication
@EnableKafkaStreams
public final class StartApplication {

    private StartApplication() {
    }

    public static void main(final String[] args) {
        SpringApplication.run(StartApplication.class, args);
    }

}
