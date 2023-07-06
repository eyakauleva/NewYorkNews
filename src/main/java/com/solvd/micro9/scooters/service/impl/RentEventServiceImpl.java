package com.solvd.micro9.scooters.service.impl;

import com.solvd.micro9.scooters.domain.RentEvent;
import com.solvd.micro9.scooters.messaging.KfProducer;
import com.solvd.micro9.scooters.service.RentEventService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RentEventServiceImpl implements RentEventService {

    private final KfProducer kfProducer;

    @Override
    public void save(final RentEvent event) {
        event.setId(UUID.randomUUID().toString());
        kfProducer.send(event);
    }

}
