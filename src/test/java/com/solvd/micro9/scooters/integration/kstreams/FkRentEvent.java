package com.solvd.micro9.scooters.integration.kstreams;

import com.solvd.micro9.scooters.domain.Currency;
import com.solvd.micro9.scooters.domain.RentEvent;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class FkRentEvent extends RentEvent {

    public FkRentEvent() {
        super.setId(UUID.randomUUID().toString());
        super.setUserId(UUID.randomUUID().toString());
        super.setPrice(BigDecimal.valueOf(87.44));
        super.setCurrency(Currency.BYN);
        super.setStarted(LocalDateTime.now());
        super.setEnded(LocalDateTime.now());
        super.setInsuranceUsed(false);
    }

}
