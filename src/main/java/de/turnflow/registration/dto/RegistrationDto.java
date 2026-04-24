package de.turnflow.registration.dto;


import de.turnflow.registration.entity.RegistrationStatus;
import lombok.*;

import java.time.OffsetDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationDto {

    private Long id;

    private Long trainingSessionId;

    private Long memberId;

    private String memberFirstName;

    private String memberLastName;

    private RegistrationStatus status;

    private OffsetDateTime registeredAt;

    private OffsetDateTime cancelledAt;
}