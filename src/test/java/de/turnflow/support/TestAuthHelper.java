package de.turnflow.support;

import com.jayway.jsonpath.JsonPath;
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
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public String loginAsAdmin() throws Exception {
        Role adminRole = roleRepository.findByName(RoleName.ROLE_ADMIN)
                .orElseThrow();

        userRepository.save(UserAccount.builder()
                .username("admin-test")
                .email("admin-test@turnflow.de")
                .passwordHash(passwordEncoder.encode("Admin123!"))
                .enabled(true)
                .roles(Set.of(adminRole))
                .build());

        String response = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType("application/json")
                        .content("""
                                {
                                  "username": "admin-test",
                                  "password": "Admin123!"
                                }
                                """))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return JsonPath.read(response, "$.accessToken");
    }
}