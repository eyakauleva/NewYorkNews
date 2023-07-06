package com.solvd.micro9.scooters.web;

import com.solvd.micro9.scooters.messaging.StreamsConfig;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StoreQueryParameters;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.config.StreamsBuilderFactoryBean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequestMapping("/money")
@RequiredArgsConstructor
public class Controller {

    private final StreamsBuilderFactoryBean factoryBean;

    @GetMapping("/{userId}")
    public ResponseEntity<BigDecimal> findValueSpentByUserToday(
            @PathVariable("userId") final String userId
    ) {
        KafkaStreams kafkaStreams = factoryBean.getKafkaStreams();
        if (kafkaStreams != null) {
            ReadOnlyKeyValueStore<String, BigDecimal> spentMoney = kafkaStreams.store(
                    StoreQueryParameters.fromNameAndType(
                            StreamsConfig.USER_EXPENSES_VIEW,
                            QueryableStoreTypes.keyValueStore()
                    )
            );
            return ResponseEntity.ok(spentMoney.get(userId));
        } else {
            return ResponseEntity.internalServerError().build();
        }
    }

}
