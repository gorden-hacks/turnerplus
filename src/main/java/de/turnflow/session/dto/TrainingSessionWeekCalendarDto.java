package de.turnflow.session.dto;

import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrainingSessionWeekCalendarDto {

    private LocalDate weekStart;

    private LocalDate weekEnd;

    private Long groupId;

    private List<TrainingSessionCalendarDayDto> days;
}