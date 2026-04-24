package de.turnflow.traininggroup.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CreateMemberGroupPermissionRequest {

    @NotNull
    private Long memberId;

    @NotNull
    private Long trainingGroupId;

    @NotNull
    private LocalDate validFrom;

    private LocalDate validTo;
}