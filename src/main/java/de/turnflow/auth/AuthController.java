package de.turnflow.auth;

import de.turnflow.auth.dto.LoginRequest;
import de.turnflow.auth.dto.LoginResponse;
import de.turnflow.auth.dto.MeResponse;
import de.turnflow.security.SecurityUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Auth")
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "Login", description = "Authentifiziert einen Benutzer und gibt ein JWT zurück.")
    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @Operation(summary = "Aktuellen Benutzer abrufen", description = "Liefert Informationen zum eingeloggten Benutzer.")
    @GetMapping("/me")
    public MeResponse me(@AuthenticationPrincipal SecurityUser user) {
        return authService.me(user.getUsername());
    }
}