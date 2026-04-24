package de.turnflow.user.dto;

import lombok.Data;

import java.util.Set;

@Data
public class UpdateUserRequest {

    private String email;

    private Boolean enabled;

    private Long memberId;

    private Set<String> roles;
}