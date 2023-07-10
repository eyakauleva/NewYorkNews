package com.solvd.micro9.scooters.web.mapper;

import com.solvd.micro9.scooters.domain.RentEvent;
import com.solvd.micro9.scooters.web.dto.RentEventDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RentEventMapper {

    RentEvent dtoToDomain(RentEventDto rentEventDto);

}
