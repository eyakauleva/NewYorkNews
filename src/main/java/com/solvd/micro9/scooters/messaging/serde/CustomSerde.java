package com.solvd.micro9.scooters.messaging.serde;

import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

public class CustomSerde<T> {

    private final Class<T> typeParameterClass;

    public CustomSerde(final Class<T> typeParameterClass) {
        this.typeParameterClass = typeParameterClass;
    }

    public Serde<T> getSerde() {
        JsonSerializer<T> serializer = new JsonSerializer<>();
        JsonDeserializer<T> deserializer = new JsonDeserializer<>(typeParameterClass);
        return Serdes.serdeFrom(serializer, deserializer);
    }

}
