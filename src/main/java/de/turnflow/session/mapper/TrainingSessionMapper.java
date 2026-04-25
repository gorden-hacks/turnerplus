package de.turnflow.session.mapper;

import de.turnflow.session.dto.TrainingSessionCalendarDto;
import de.turnflow.session.dto.TrainingSessionDto;
import de.turnflow.session.dto.UpdateTrainingSessionRequest;
import de.turnflow.session.entity.TrainingSession;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TrainingSessionMapper {

    @Mapping(source = "trainingGroup.id", target = "trainingGroupId")
    @Mapping(source = "trainingGroup.name", target = "trainingGroupName")
    @Mapping(target = "registeredCount", ignore = true)
    @Mapping(target = "waitlistCount", ignore = true)
    TrainingSessionDto toDto(TrainingSession entity);

    @Mapping(source = "trainingGroup.id", target = "trainingGroupId")
    @Mapping(source = "trainingGroup.name", target = "trainingGroupName")
    @Mapping(target = "registeredCount", ignore = true)
    TrainingSessionCalendarDto toCalendarDto(TrainingSession entity);

    List<TrainingSessionDto> toDtoList(List<TrainingSession> entities);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "trainingGroup", ignore = true)
    void update(UpdateTrainingSessionRequest request, @MappingTarget TrainingSession entity);
}