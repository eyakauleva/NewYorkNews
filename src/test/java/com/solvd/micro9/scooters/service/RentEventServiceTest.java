package com.solvd.micro9.scooters.service;

import com.solvd.micro9.scooters.domain.Currency;
import com.solvd.micro9.scooters.domain.RentEvent;
import com.solvd.micro9.scooters.messaging.RentEventProducer;
import com.solvd.micro9.scooters.service.impl.RentEventServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@ExtendWith(MockitoExtension.class)
public class RentEventServiceTest {

    @Mock
    private RentEventProducer producer;

    @InjectMocks
    private RentEventServiceImpl rentEventService;

    @Test
    void verifyEventIsSaved() {
        RentEvent event = RentEvent.builder()
                .userId("dsv3r423")
                .price(BigDecimal.valueOf(4342))
                .currency(Currency.BYN)
                .started(LocalDateTime.now())
                .ended(LocalDateTime.now())
                .isInsuranceUsed(false)
                .build();
        rentEventService.save(event);
        Assertions.assertNotNull(event.getId());
        Mockito.verify(producer, Mockito.times(1)).send(event);
    }

}
