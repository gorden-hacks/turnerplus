package de.turnflow.support;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.turnflow.auth.dto.LoginRequest;
import de.turnflow.user.*;
import de.turnflow.user.entity.Role;
import de.turnflow.user.entity.RoleName;
import de.turnflow.user.entity.UserAccount;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Component
@RequiredArgsConstructor
public class TestAuthHelper {

    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public String loginAsAdmin() throws Exception {
        return login("admin-test", "Admin123!", RoleName.ROLE_ADMIN);
    }

    public String loginAsTrainer() throws Exception {
        return login("trainer-test", "Trainer123!", RoleName.ROLE_TRAINER);
    }

    public String loginAsMember() throws Exception {
        return login("member-test", "Member123!", RoleName.ROLE_MEMBER);
    }

    private String login(String username, String password, RoleName roleName) throws Exception {

        Role role = roleRepository.findByName(roleName)
                .orElseGet(() -> roleRepository.save(
                        Role.builder().name(roleName).build()
                ));

        if (!userRepository.existsByUsername(username)) {
            userRepository.save(UserAccount.builder()
                    .username(username)
                    .email(username + "@turnflow.de")
                    .passwordHash(passwordEncoder.encode(password))
                    .enabled(true)
                    .roles(Set.of(role))
                    .build());
        }

        String response = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(new LoginRequest(username, password))))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return extractToken(response);
    }

    private String extractToken(String json) {
        try {
            return objectMapper.readTree(json)
                    .get("accessToken")
                    .asText();
        } catch (Exception e) {
            throw new RuntimeException("Token konnte nicht gelesen werden: " + json, e);
        }
    }

    public String bearer(String token) {
        return "Bearer " + token;
    }
}