package com.solvd.micro9.scooters.integration.kstreams;

import com.solvd.micro9.scooters.domain.Currency;
import com.solvd.micro9.scooters.domain.RentEvent;
import com.solvd.micro9.scooters.messaging.KStreamConfig;
import com.solvd.micro9.scooters.messaging.serde.CustomSerdes;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.Serializer;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.TestInputTopic;
import org.apache.kafka.streams.TestOutputTopic;
import org.apache.kafka.streams.TopologyTestDriver;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.test.annotation.DirtiesContext;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootTest(classes = {KStreamConfig.class, StreamsBuilder.class})
@DirtiesContext
public class KStreamsIT extends KafkaTestcontainers {

    @Autowired
    private KStreamConfig kStreamConfig;
    private TopologyTestDriver testDriver;
    private TestInputTopic<String, RentEvent> inputTopic;
    private TestOutputTopic<String, BigDecimal> outputTopic;

    @BeforeEach
    void setup() {
        StreamsBuilder streamsBuilder = new StreamsBuilder();
        kStreamConfig.buildPipeline(streamsBuilder);
        testDriver = new TopologyTestDriver(
                streamsBuilder.build(), this.getKafkaStreamProperties()
        );
        Serializer<RentEvent> rentEventSerializer = CustomSerdes.RentEvent().serializer();
        Map<String, Object> props = new HashMap<>();
        props.put(JsonSerializer.ADD_TYPE_INFO_HEADERS, false);
        rentEventSerializer.configure(props, false);
        inputTopic = testDriver.createInputTopic(
                "rent-events",
                Serdes.String().serializer(),
                rentEventSerializer
        );
        outputTopic = testDriver.createOutputTopic(
                "income",
                Serdes.String().deserializer(),
                CustomSerdes.BigDecimal().deserializer()
        );
    }

    @Test
    void verifyInputTopicProcessedAndSentToOutputTopic() {
        Assertions.assertTrue(outputTopic.isEmpty());
        RentEvent rentEvent = new FkRentEvent();
        RentEvent rentEventWithUSD = new FkRentEvent();
        rentEventWithUSD.setCurrency(Currency.USD);
        RentEvent rentEventEndedNotToday = new FkRentEvent();
        rentEventEndedNotToday.setEnded(LocalDateTime.now().withYear(2020));
        List<RentEvent> rentEvents = List.of(
                rentEvent,
                rentEventWithUSD,
                rentEventEndedNotToday
        );
        rentEvents.forEach(event -> inputTopic.pipeInput(event.getId(), event));


        List<BigDecimal> resultValues = outputTopic.readValuesToList();
        System.out.println("RES = " + resultValues);

        Assertions.assertEquals(resultValues.size(), rentEvents.size());

        //Assertions.assertEquals(outputTopic.readValue(), rentEvent.getPrice());
        Assertions.assertTrue(outputTopic.isEmpty());
    }

}
