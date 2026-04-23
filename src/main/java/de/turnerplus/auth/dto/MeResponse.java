package de.turnerplus.auth.dto;

import lombok.Builder;
import lombok.Data;

import java.util.Set;

@Data
@Builder
public class MeResponse {
    private Long userId;
    private String username;
    private String email;
    private boolean enabled;
    private Long memberId;
    private Set<String> roles;
}