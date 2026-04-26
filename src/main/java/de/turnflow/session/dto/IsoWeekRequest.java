package de.turnflow.session.dto;

import de.turnflow.common.validation.ValidIsoWeek;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
@ValidIsoWeek
public class IsoWeekRequest {

    @Min(1)
    private int year;

    @Min(1)
    private int week;

    private Long groupId;
}