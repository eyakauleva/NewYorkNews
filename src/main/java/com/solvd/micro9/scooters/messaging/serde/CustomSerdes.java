package com.solvd.micro9.scooters.messaging.serde;

import com.solvd.micro9.scooters.domain.RentEvent;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.math.BigDecimal;

@Configuration
public class CustomSerdes {

    @Bean
    public Serde<RentEvent> rentEventSerde() {
        JsonSerializer<RentEvent> serializer = new JsonSerializer<>();
        JsonDeserializer<RentEvent> deserializer = new JsonDeserializer<>(RentEvent.class);
        return Serdes.serdeFrom(serializer, deserializer);
    }

    @Bean
    public Serde<BigDecimal> bigDecimalSerde() {
        JsonSerializer<BigDecimal> serializer = new JsonSerializer<>();
        JsonDeserializer<BigDecimal> deserializer
                = new JsonDeserializer<>(BigDecimal.class);
        return Serdes.serdeFrom(serializer, deserializer);
    }

}
