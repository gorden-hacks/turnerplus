package de.turnflow.traininggroup.mapper;

import de.turnflow.traininggroup.dto.TrainerGroupAssignmentDto;
import de.turnflow.traininggroup.entity.TrainerGroupAssignment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TrainerGroupAssignmentMapper {

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "user.username", target = "username")
    @Mapping(source = "trainingGroup.id", target = "trainingGroupId")
    @Mapping(source = "trainingGroup.name", target = "trainingGroupName")
    TrainerGroupAssignmentDto toDto(TrainerGroupAssignment entity);
}