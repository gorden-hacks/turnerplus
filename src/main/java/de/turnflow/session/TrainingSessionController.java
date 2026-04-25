package de.turnflow.session;

import de.turnflow.session.dto.*;
import de.turnflow.session.entity.TrainingSessionStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.List;

@Tag(name = "Training Sessions")
@RestController
@RequestMapping("/api/v1/training-sessions")
@RequiredArgsConstructor
public class TrainingSessionController {

    private final TrainingSessionService trainingSessionService;

    @Operation(
            summary = "Trainingseinheiten filtern",
            description = "Liefert Trainingseinheiten optional gefiltert nach Gruppe, Status und Zeitraum."
    )
    @GetMapping
    public List<TrainingSessionDto> getFiltered(
            @RequestParam(required = false) Long groupId,
            @RequestParam(required = false) TrainingSessionStatus status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime to
    ) {
        return trainingSessionService.findFiltered(groupId, status, from, to);
    }

    @Operation(summary = "Trainingseinheit nach ID abrufen")
    @GetMapping("/{id}")
    public TrainingSessionDto getById(@PathVariable Long id) {
        return trainingSessionService.findById(id);
    }

    @Operation(summary = "Trainingseinheit erstellen")
    @PostMapping
    public TrainingSessionDto create(@Valid @RequestBody CreateTrainingSessionRequest request) {
        return trainingSessionService.create(request);
    }

    @Operation(summary = "Trainingseinheit aktualisieren")
    @PutMapping("/{id}")
    public TrainingSessionDto update(
            @PathVariable Long id,
            @RequestBody UpdateTrainingSessionRequest request
    ) {
        return trainingSessionService.update(id, request);
    }

    @Operation(summary = "Status einer Trainingseinheit ändern")
    @PatchMapping("/{id}/status")
    public TrainingSessionDto updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateTrainingSessionStatusRequest request
    ) {
        return trainingSessionService.updateStatus(id, request);
    }

    @Operation(summary = "Trainingseinheit löschen")
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        trainingSessionService.delete(id);
    }

    @Operation(
            summary = "Kalenderansicht abrufen",
            description = "Liefert Trainingseinheiten für einen Zeitraum, optimiert für Kalender- oder Wochenansichten."
    )
    @GetMapping("/calendar")
    public List<TrainingSessionCalendarDto> calendar(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime to,
            @RequestParam(required = false) Long groupId
    ) {
        return trainingSessionService.calendar(from, to, groupId);
    }
}