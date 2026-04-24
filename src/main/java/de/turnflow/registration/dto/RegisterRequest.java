package de.turnflow.registration.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RegisterRequest {

    @NotNull
    private Long memberId;
}