package com.solvd.micro9.scooters.integration.kstreams;

import com.solvd.micro9.scooters.domain.Currency;
import com.solvd.micro9.scooters.domain.RentEvent;
import com.solvd.micro9.scooters.messaging.KStreamConfig;
import com.solvd.micro9.scooters.messaging.serde.CustomSerde;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.Serializer;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.TestInputTopic;
import org.apache.kafka.streams.TestOutputTopic;
import org.apache.kafka.streams.TopologyTestDriver;
import org.apache.kafka.streams.state.KeyValueStore;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.test.annotation.DirtiesContext;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootTest(classes = {KStreamConfig.class, StreamsBuilder.class})
@DirtiesContext
public class KStreamsIT extends KafkaTestcontainers {

    @Autowired
    private KStreamConfig kStreamConfig;

    private final Serde<RentEvent> rentEventSerde =
            new CustomSerde<>(RentEvent.class).getSerde();

    private final Serde<BigDecimal> bigDecimalSerde =
            new CustomSerde<>(BigDecimal.class).getSerde();

    private TopologyTestDriver testDriver;

    private TestInputTopic<String, RentEvent> inputTopic;

    private TestOutputTopic<String, BigDecimal> outputTopic;

    private KeyValueStore<String, BigDecimal> usersExpensesStore;

    @BeforeEach
    void setup() {
        StreamsBuilder streamsBuilder = new StreamsBuilder();
        kStreamConfig.buildPipeline(streamsBuilder);
        testDriver = new TopologyTestDriver(
                streamsBuilder.build(), this.getKafkaStreamProperties()
        );
        Serializer<RentEvent> rentEventSerializer = rentEventSerde.serializer();
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
                bigDecimalSerde.deserializer()
        );
        usersExpensesStore =
                testDriver.getKeyValueStore(KStreamConfig.USER_EXPENSES_STORE);
    }

    @AfterEach
    void teardown() {
        testDriver.close();
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
        BigDecimal resultIncome = BigDecimal.ZERO;
        for (RentEvent event : rentEvents) {
            inputTopic.pipeInput(event.getId(), event);
            if (event.getCurrency().equals(Currency.USD)) {
                BigDecimal convertedToByn = event.getPrice().multiply(
                        BigDecimal.valueOf(KStreamConfig.USD_TO_BYN_RATE)
                );
                resultIncome = resultIncome.add(convertedToByn);
            } else {
                resultIncome = resultIncome.add(event.getPrice());
            }
        }
        List<BigDecimal> resultValues = outputTopic.readValuesToList();
        Assertions.assertEquals(rentEvents.size(), resultValues.size());
        BigDecimal finalResultValue = resultValues.get(resultValues.size() - 1);
        Assertions.assertEquals(resultIncome, finalResultValue);
        Assertions.assertTrue(outputTopic.isEmpty());
    }

    @Test
    void verifyUsersExpensesSavedToStateStore() {
        String userId = "11111";
        RentEvent rentEvent = new FkRentEvent();
        RentEvent rentEventWithUSD = new FkRentEvent();
        rentEventWithUSD.setUserId(userId);
        rentEventWithUSD.setCurrency(Currency.USD);
        RentEvent rentEventEndedNotToday = new FkRentEvent();
        rentEventEndedNotToday.setUserId(userId);
        rentEventEndedNotToday.setEnded(LocalDateTime.now().withYear(2020));
        List<RentEvent> rentEvents = List.of(
                rentEvent,
                rentEventWithUSD,
                rentEventEndedNotToday
        );
        BigDecimal userExpectedIncome = BigDecimal.ZERO;
        for (RentEvent event : rentEvents) {
            inputTopic.pipeInput(event.getId(), event);
            if (event.getUserId().equals(userId)
                    && event.getEnded().toLocalDate().equals(LocalDate.now())) {
                if (event.getCurrency().equals(Currency.USD)) {
                    BigDecimal convertedToByn = event.getPrice().multiply(
                            BigDecimal.valueOf(KStreamConfig.USD_TO_BYN_RATE)
                    );
                    userExpectedIncome = userExpectedIncome.add(convertedToByn);
                } else {
                    userExpectedIncome = userExpectedIncome.add(event.getPrice());
                }
            }
        }
        BigDecimal userResultIncome = usersExpensesStore.get(userId);
        Assertions.assertNotNull(userResultIncome);
        Assertions.assertEquals(userExpectedIncome, userResultIncome);
    }

}
