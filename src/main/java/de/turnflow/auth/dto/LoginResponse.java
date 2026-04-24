package de.turnflow.auth.dto;

import lombok.Builder;
import lombok.Data;

import java.util.Set;

@Data
@Builder
public class LoginResponse {
    private String accessToken;
    private String tokenType;
    private Long expiresIn;
    private String username;
    private Set<String> roles;
}