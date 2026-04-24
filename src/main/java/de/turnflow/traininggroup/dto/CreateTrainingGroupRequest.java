package de.turnflow.traininggroup.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateTrainingGroupRequest {

    @NotBlank
    private String name;

    private String description;
}