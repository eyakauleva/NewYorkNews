package com.solvd.micro9.scooters.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RentEvent {

    private String id;
    private String userId;
    private BigDecimal price;
    private Currency currency;
    private LocalDateTime started;
    private LocalDateTime ended;
    private boolean isInsuranceUsed;

}
