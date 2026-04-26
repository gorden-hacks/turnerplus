package de.turnflow.me;

import de.turnflow.security.SecurityUser;
import de.turnflow.session.TrainingSessionService;
import de.turnflow.session.dto.MyTrainingSessionDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.List;

@Tag(name = "Me")
@RestController
@RequestMapping("/api/v1/me")
@RequiredArgsConstructor
public class MeController {

    private final TrainingSessionService trainingSessionService;

    @Operation(summary = "Meine sichtbaren Trainingseinheiten abrufen")
    @GetMapping("/training-sessions")
    public List<MyTrainingSessionDto> getMyTrainingSessions(
            @AuthenticationPrincipal SecurityUser user,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            OffsetDateTime from,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            OffsetDateTime to
    ) {
        return trainingSessionService.findMyTrainingSessions(user.getId(), from, to);
    }
}