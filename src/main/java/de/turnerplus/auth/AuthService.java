package de.turnerplus.auth;

import de.turnerplus.auth.dto.LoginRequest;
import de.turnerplus.auth.dto.LoginResponse;
import de.turnerplus.auth.dto.MeResponse;
import de.turnerplus.common.exception.NotFoundException;
import de.turnerplus.security.JwtService;
import de.turnerplus.user.UserRepository;
import de.turnerplus.user.entity.UserAccount;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public LoginResponse login(LoginRequest request) {
        UserAccount user = userRepository
                .findByUsernameOrEmail(request.getUsername(), request.getUsername())
                .orElseThrow(() -> new BadCredentialsException("Ungültige Anmeldedaten"));

        if (!user.isEnabled()) {
            throw new BadCredentialsException("Benutzerkonto ist deaktiviert");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new BadCredentialsException("Ungültige Anmeldedaten");
        }

        Set<String> roles = user.getRoles()
                .stream()
                .map(role -> role.getName().name())
                .collect(Collectors.toSet());

        String accessToken = jwtService.generateToken(user);

        return LoginResponse.builder()
                .accessToken(accessToken)
                .tokenType("Bearer")
                .expiresIn(jwtService.getAccessTokenExpirationSeconds())
                .username(user.getUsername())
                .roles(roles)
                .build();
    }

    public MeResponse me(String username) {
        UserAccount user = userRepository.findByUsernameOrEmail(username, username)
                .orElseThrow(() -> new NotFoundException("Benutzer nicht gefunden: " + username));

        return MeResponse.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .enabled(user.isEnabled())
                .memberId(user.getMember() != null ? user.getMember().getId() : null)
                .roles(user.getRoles()
                        .stream()
                        .map(role -> role.getName().name())
                        .collect(Collectors.toSet()))
                .build();
    }
}