package de.turnflow.registration;

import de.turnflow.registration.dto.RegisterRequest;
import de.turnflow.registration.dto.RegistrationDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Registrations")
@RestController
@RequestMapping("/api/v1/sessions/{sessionId}")
@RequiredArgsConstructor
public class RegistrationController {

    private final RegistrationService registrationService;

    @Operation(summary = "Mitglied zu Trainingseinheit anmelden")
    @PostMapping("/register")
    public RegistrationDto register(
            @PathVariable Long sessionId,
            @Valid @RequestBody RegisterRequest request
    ) {
        return registrationService.register(sessionId, request.getMemberId());
    }

    @Operation(summary = "Mitglied von Trainingseinheit abmelden")
    @PostMapping("/unregister")
    public RegistrationDto unregister(
            @PathVariable Long sessionId,
            @Valid @RequestBody RegisterRequest request
    ) {
        return registrationService.unregister(sessionId, request.getMemberId());
    }
}