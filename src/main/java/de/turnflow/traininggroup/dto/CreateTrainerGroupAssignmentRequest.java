package de.turnflow.traininggroup.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateTrainerGroupAssignmentRequest {

    @NotNull
    private Long userId;

    @NotNull
    private Long trainingGroupId;
}