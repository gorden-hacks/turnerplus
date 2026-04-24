package de.turnflow.traininggroup.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrainingGroupDto {
    private Long id;
    private String name;
    private String description;
    private boolean active;
}