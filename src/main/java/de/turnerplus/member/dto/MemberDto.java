package de.turnerplus.member.dto;

import lombok.*;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberDto {
    private Long id;
    private String firstName;
    private String lastName;
    private LocalDate birthDate;
    private String photoUrl;
    private boolean active;
}