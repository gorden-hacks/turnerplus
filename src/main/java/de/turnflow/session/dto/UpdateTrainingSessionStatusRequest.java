package de.turnflow.session.dto;


import de.turnflow.session.entity.TrainingSessionStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateTrainingSessionStatusRequest {

    @NotNull
    private TrainingSessionStatus status;
}