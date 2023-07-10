package com.solvd.micro9.scooters.web;

import com.solvd.micro9.scooters.service.KStreamsService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.config.StreamsBuilderFactoryBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.math.BigDecimal;

@WebMvcTest(ExpenseController.class)
public class ExpenseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StreamsBuilderFactoryBean factoryBean;

    @MockBean
    private KStreamsService kStreamsService;

    @Test
    @SneakyThrows
    void verifyMoneySpentByUserTodayAreReceived() {
        String userId = "4fs3213";
        BigDecimal expected = BigDecimal.valueOf(32.6342);
        Mockito.when(kStreamsService.countMoneySpentByUserToday(userId))
                .thenReturn(expected);
        MvcResult mvcResult = mockMvc
                .perform(
                        MockMvcRequestBuilders.get("/expense/{userId}", userId)
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        Mockito.verify(kStreamsService, Mockito.times(1))
                .countMoneySpentByUserToday(Mockito.anyString());
        String result = mvcResult.getResponse().getContentAsString();
        Assertions.assertNotNull(result);
        Assertions.assertEquals(expected.toString(), result);
    }

}
