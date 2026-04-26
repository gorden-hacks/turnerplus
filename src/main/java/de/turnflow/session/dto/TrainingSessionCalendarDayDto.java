package de.turnflow.session.dto;

import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrainingSessionCalendarDayDto {

    private LocalDate date;

    private List<TrainingSessionCalendarDto> sessions;
}