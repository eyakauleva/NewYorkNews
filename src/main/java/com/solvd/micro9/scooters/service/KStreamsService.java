package com.solvd.micro9.scooters.service;

import java.math.BigDecimal;

public interface KStreamsService {

    BigDecimal countMoneySpentByUserToday(String userId);

}
