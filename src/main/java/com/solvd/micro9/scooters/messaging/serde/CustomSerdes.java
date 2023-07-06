package com.solvd.micro9.scooters.messaging.serde;

import com.solvd.micro9.scooters.domain.RentEvent;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.math.BigDecimal;

public final class CustomSerdes {

    private CustomSerdes() {
    }

    public static Serde<RentEvent> RentEvent() {
        JsonSerializer<RentEvent> serializer = new JsonSerializer<>();
        JsonDeserializer<RentEvent> deserializer = new JsonDeserializer<>(RentEvent.class);
        return Serdes.serdeFrom(serializer, deserializer);
    }

    public static Serde<BigDecimal> BigDecimal() {
        JsonSerializer<BigDecimal> serializer = new JsonSerializer<>();
        JsonDeserializer<BigDecimal> deserializer
                = new JsonDeserializer<>(BigDecimal.class);
        return Serdes.serdeFrom(serializer, deserializer);
    }

}
