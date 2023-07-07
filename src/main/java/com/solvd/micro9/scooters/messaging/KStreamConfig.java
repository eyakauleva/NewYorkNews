package com.solvd.micro9.scooters.messaging;

import com.solvd.micro9.scooters.domain.Currency;
import com.solvd.micro9.scooters.domain.RentEvent;
import com.solvd.micro9.scooters.messaging.serde.CustomSerdes;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Branched;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Grouped;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Named;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.state.Stores;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

@Component
public class KStreamConfig {

    public static final String USER_EXPENSES_VIEW = "user-expense";
    public static final double USD_TO_BYN_RATE = 3.0;
    private static final String SINK_TOPIC = "income";
    private static final String SPLIT_BY_CURRENCY = "split-by-currency-";
    private static final String USD_BRANCH = Currency.USD.name();
    private static final String BYN_BRANCH = Currency.BYN.name();

    @Value("${spring.kafka.topic}")
    private String sourceTopic;

    @Autowired
    public void buildPipeline(final StreamsBuilder streamsBuilder) {
        Map<String, KStream<String, RentEvent>> eventsStream = streamsBuilder
                .stream(
                        sourceTopic,
                        Consumed.with(Serdes.String(), CustomSerdes.RentEvent())
                )
                .split(Named.as(SPLIT_BY_CURRENCY))
                .branch(
                        (key, value) -> value.getCurrency().equals(Currency.USD),
                        Branched.as(USD_BRANCH)
                )
                .branch(
                        (key, value) -> value.getCurrency().equals(Currency.BYN),
                        Branched.as(BYN_BRANCH)
                )
                .noDefaultBranch();
        KStream<String, RentEvent> usdConvertedToByn = eventsStream
                .get(SPLIT_BY_CURRENCY + USD_BRANCH)
                .mapValues(value -> {
                    BigDecimal exchangeRate = BigDecimal.valueOf(USD_TO_BYN_RATE);
                    value.setPrice(value.getPrice().multiply(exchangeRate));
                    value.setCurrency(Currency.BYN);
                    return value;
                });
        KStream<String, RentEvent> mergedStreams = eventsStream
                .get(SPLIT_BY_CURRENCY + BYN_BRANCH)
                .merge(usdConvertedToByn);
        mergedStreams
                .filter((key, value) -> value.getEnded()
                        .toLocalDate()
                        .equals(LocalDate.now()))
                .map((key, value) -> new KeyValue<>(value.getUserId(), value.getPrice()))
                .groupByKey(Grouped.with(Serdes.String(), CustomSerdes.BigDecimal()))
                .aggregate(
                        () -> BigDecimal.ZERO,
                        (key, value, aggregate) -> value.add(aggregate),
                        Materialized.<String, BigDecimal>as(
                                        Stores.persistentKeyValueStore(USER_EXPENSES_VIEW)
                                )
                                .withKeySerde(Serdes.String())
                                .withValueSerde(CustomSerdes.BigDecimal())
                );
        mergedStreams
                .selectKey((key, value) -> "income")
                .mapValues(RentEvent::getPrice)
                .groupByKey(Grouped.with(Serdes.String(), CustomSerdes.BigDecimal()))
                .reduce(BigDecimal::add)
                .toStream()
                .to(SINK_TOPIC, Produced.with(Serdes.String(), CustomSerdes.BigDecimal()));
    }

}
