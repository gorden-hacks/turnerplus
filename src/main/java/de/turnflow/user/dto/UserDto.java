package de.turnflow.user.dto;

import lombok.*;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

    private Long id;

    private String username;

    private String email;

    private boolean enabled;

    private Long memberId;

    private Set<String> roles;
}