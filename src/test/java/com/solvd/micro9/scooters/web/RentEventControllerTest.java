package com.solvd.micro9.scooters.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.solvd.micro9.scooters.domain.Currency;
import com.solvd.micro9.scooters.service.RentEventService;
import com.solvd.micro9.scooters.web.dto.RentEventDto;
import com.solvd.micro9.scooters.web.mapper.RentEventMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.config.StreamsBuilderFactoryBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@WebMvcTest(RentEventController.class)
public class RentEventControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private StreamsBuilderFactoryBean factoryBean;

    @MockBean
    private RentEventService rentEventService;

    @MockBean
    private RentEventMapper rentEventMapper;

    @Test
    @SneakyThrows
    void verifyEventIsSaved() {
        RentEventDto eventDto = RentEventDto.builder()
                .userId("dsv3r423")
                .price(BigDecimal.valueOf(4342))
                .currency(Currency.BYN)
                .started(LocalDateTime.now())
                .ended(LocalDateTime.now())
                .isInsuranceUsed(false)
                .build();
        mockMvc
                .perform(
                        MockMvcRequestBuilders.post("/rent")
                                .contentType("application/json;charset=UTF-8")
                                .content(objectMapper.writeValueAsString(eventDto))
                )
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @SneakyThrows
    void verifyEventValidationFailed() {
        RentEventDto eventDto = RentEventDto.builder()
                .build();
        mockMvc
                .perform(
                        MockMvcRequestBuilders.post("/rent")
                                .contentType("application/json;charset=UTF-8")
                                .content(objectMapper.writeValueAsString(eventDto))
                )
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

}
