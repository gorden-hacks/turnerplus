package de.turnflow.session.mapper;

import de.turnflow.session.TrainingSessionWithCountsProjection;
import de.turnflow.session.dto.TrainingSessionCalendarDto;
import de.turnflow.session.dto.TrainingSessionDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TrainingSessionProjectionMapper {

    TrainingSessionDto toDto(TrainingSessionWithCountsProjection projection);

    TrainingSessionCalendarDto toCalendarDto(TrainingSessionWithCountsProjection projection);
}