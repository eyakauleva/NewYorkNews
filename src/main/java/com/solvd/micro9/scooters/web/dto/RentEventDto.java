package com.solvd.micro9.scooters.web.dto;

import com.solvd.micro9.scooters.domain.Currency;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
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
public class RentEventDto {

    private String id;

    @NotBlank(message = "User id cannot be empty")
    private String userId;

    @NotNull(message = "Rent price cannot be empty")
    @PositiveOrZero(message = "Rent price cannot be negative")
    private BigDecimal price;

    @NotNull(message = "Rent currency cannot be empty")
    private Currency currency;

    @NotNull(message = "Rent start time cannot be empty")
    private LocalDateTime started;

    @NotNull(message = "Rent end time cannot be empty")
    private LocalDateTime ended;

    private boolean isInsuranceUsed;

}
