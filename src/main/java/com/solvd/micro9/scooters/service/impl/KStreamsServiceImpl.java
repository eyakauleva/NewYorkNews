package com.solvd.micro9.scooters.service.impl;

import com.solvd.micro9.scooters.messaging.StreamsConfig;
import com.solvd.micro9.scooters.service.KStreamsService;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StoreQueryParameters;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.springframework.kafka.config.StreamsBuilderFactoryBean;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class KStreamsServiceImpl implements KStreamsService {

    private final StreamsBuilderFactoryBean factoryBean;

    @Override
    public BigDecimal countMoneySpentByUserToday(final String userId) {
        KafkaStreams kafkaStreams = factoryBean.getKafkaStreams();
        if (kafkaStreams != null) {
            ReadOnlyKeyValueStore<String, BigDecimal> usersExpenses = kafkaStreams.store(
                    StoreQueryParameters.fromNameAndType(
                            StreamsConfig.USER_EXPENSES_VIEW,
                            QueryableStoreTypes.keyValueStore()
                    )
            );
            return usersExpenses.get(userId);
        } else {
            throw new RuntimeException("No available kafka streams");
        }
    }

}
