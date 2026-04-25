package de.turnflow.traininggroup.dto;

import de.turnflow.common.validation.ValidDateRange;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
@ValidDateRange(from = "validFrom", to = "validTo")
public class CreateMemberGroupPermissionRequest {

    @NotNull
    private Long memberId;

    @NotNull
    private Long trainingGroupId;

    @NotNull
    private LocalDate validFrom;

    private LocalDate validTo;
}