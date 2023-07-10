package com.solvd.micro9.scooters.web;

import com.solvd.micro9.scooters.service.KStreamsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/expense")
public class ExpenseController {

    private final KStreamsService kStreamsService;

    @GetMapping("/{userId}")
    public BigDecimal countMoneySpentByUserToday(
            @PathVariable("userId") final String userId
    ) {
        return kStreamsService.countMoneySpentByUserToday(userId);
    }

}
