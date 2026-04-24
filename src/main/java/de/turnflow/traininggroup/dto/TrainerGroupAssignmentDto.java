package de.turnflow.traininggroup.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrainerGroupAssignmentDto {
    private Long id;
    private Long userId;
    private String username;
    private Long trainingGroupId;
    private String trainingGroupName;
}