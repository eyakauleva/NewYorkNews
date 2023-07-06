package com.solvd.micro9.scooters.messaging;

import com.solvd.micro9.scooters.domain.RentEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KfProducer {

    @Value("${spring.kafka.topic}")
    private String topic;

    private final KafkaTemplate<String, RentEvent> kafkaTemplate;

    public void send(final RentEvent event) {
        kafkaTemplate.send(topic, event.getId(), event);
    }

}
