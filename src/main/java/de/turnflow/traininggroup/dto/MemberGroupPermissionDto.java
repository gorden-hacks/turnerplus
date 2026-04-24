package de.turnflow.traininggroup.dto;

import lombok.*;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberGroupPermissionDto {
    private Long id;
    private Long memberId;
    private String memberFirstName;
    private String memberLastName;
    private Long trainingGroupId;
    private String trainingGroupName;
    private LocalDate validFrom;
    private LocalDate validTo;
    private boolean active;
}