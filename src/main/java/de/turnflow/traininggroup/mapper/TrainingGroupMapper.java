package de.turnflow.traininggroup.mapper;


import de.turnflow.traininggroup.dto.CreateTrainingGroupRequest;
import de.turnflow.traininggroup.dto.TrainingGroupDto;
import de.turnflow.traininggroup.dto.UpdateTrainingGroupRequest;
import de.turnflow.traininggroup.entity.TrainingGroup;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TrainingGroupMapper {

    TrainingGroupDto toDto(TrainingGroup entity);

    List<TrainingGroupDto> toDtoList(List<TrainingGroup> entities);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", constant = "true")
    TrainingGroup toEntity(CreateTrainingGroupRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    void update(UpdateTrainingGroupRequest request, @MappingTarget TrainingGroup entity);
}