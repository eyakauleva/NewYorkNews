package com.solvd.micro9.scooters.service;

import com.solvd.micro9.scooters.service.impl.KStreamsServiceImpl;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StoreQueryParameters;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.config.StreamsBuilderFactoryBean;

import java.math.BigDecimal;

@ExtendWith(MockitoExtension.class)
public class KStreamServiceTest {

    @Mock
    private StreamsBuilderFactoryBean factoryBean;

    @InjectMocks
    private KStreamsServiceImpl kStreamsService;

    @Test
    void verifyMoneySpentByUserTodayReceived() {
        BigDecimal expected = BigDecimal.valueOf(34.432423);
        String userId = "5434nsnansw";
        ReadOnlyKeyValueStore<String, BigDecimal> userExpenses
                = Mockito.mock(ReadOnlyKeyValueStore.class);
        Mockito.when(userExpenses.get(userId)).thenReturn(expected);
        KafkaStreams kafkaStreams = Mockito.mock(KafkaStreams.class);
        Mockito.when(kafkaStreams.store(Mockito.any(StoreQueryParameters.class)))
                .thenReturn(userExpenses);
        Mockito.when(factoryBean.getKafkaStreams()).thenReturn(kafkaStreams);
        BigDecimal result = kStreamsService.countMoneySpentByUserToday(userId);
        Assertions.assertNotNull(result);
        Assertions.assertEquals(expected, result);
        Mockito.verify(factoryBean, Mockito.times(1)).getKafkaStreams();
        Mockito.verify(kafkaStreams, Mockito.times(1))
                .store(Mockito.any(StoreQueryParameters.class));
        Mockito.verify(userExpenses, Mockito.times(1))
                .get(Mockito.anyString());
    }

    @Test
    void verifyMoneySpentByUserTodayThrowsException() {
        String userId = "23fsda";
        Mockito.when(factoryBean.getKafkaStreams()).thenReturn(null);
        Assertions.assertThrows(
                RuntimeException.class,
                () -> kStreamsService.countMoneySpentByUserToday(userId)
        );
    }

}
