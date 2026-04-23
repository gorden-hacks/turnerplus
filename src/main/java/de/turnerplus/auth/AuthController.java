package de.turnerplus.auth;

import de.turnerplus.auth.dto.LoginRequest;
import de.turnerplus.auth.dto.LoginResponse;
import de.turnerplus.auth.dto.MeResponse;
import de.turnerplus.security.SecurityUser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @GetMapping("/me")
    public MeResponse me(@AuthenticationPrincipal SecurityUser user) {
        return authService.me(user.getUsername());
    }
}