package com.solvd.micro9.scooters.web;

import com.solvd.micro9.scooters.domain.RentEvent;
import com.solvd.micro9.scooters.service.RentEventService;
import com.solvd.micro9.scooters.web.dto.RentEventDto;
import com.solvd.micro9.scooters.web.mapper.RentEventMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/rent")
public class RentEventController {

    private final RentEventService rentEventService;
    private final RentEventMapper rentEventMapper;

    @PostMapping
    public void save(@RequestBody @Valid final RentEventDto eventDto) {
        RentEvent event = rentEventMapper.dtoToDomain(eventDto);
        rentEventService.save(event);
    }

}
