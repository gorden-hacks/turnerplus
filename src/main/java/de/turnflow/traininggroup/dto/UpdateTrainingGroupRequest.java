package de.turnflow.traininggroup.dto;

import lombok.Data;

@Data
public class UpdateTrainingGroupRequest {
    private String name;
    private String description;
    private Boolean active;
}