package de.turnflow.registration;

import de.turnflow.common.exception.ErrorCode;
import de.turnflow.member.entity.Member;
import de.turnflow.registration.entity.RegistrationStatus;
import de.turnflow.session.entity.TrainingSession;
import de.turnflow.session.entity.TrainingSessionStatus;
import de.turnflow.support.BaseApiIT;
import de.turnflow.traininggroup.entity.TrainingGroup;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.time.OffsetDateTime;

import static de.turnflow.support.ApiJsonAssertions.expectErrorCode;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class RegistrationControllerIT extends BaseApiIT {

    @Test
    void register_shouldRegisterMemberSuccessfully() throws Exception {
        TrainingGroup group = data.group("Wettkampfgruppe");
        TrainingSession session = createSession(group);

        Member member = data.member("Max", "Mustermann");
        data.memberGroupPermission(member, group);

        mockMvc.perform(post("/api/v1/sessions/{id}/register", session.getId())
                        .header("Authorization", bearer(adminToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "memberId": %d
                                }
                                """.formatted(member.getId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.memberId").value(member.getId()))
                .andExpect(jsonPath("$.trainingSessionId").value(session.getId()))
                .andExpect(jsonPath("$.status").value("REGISTERED"));
    }

    @Test
    void register_shouldReturnError_whenMemberHasNoPermissions() throws Exception {
        TrainingGroup group = data.group("Wettkampfgruppe");
        TrainingSession session = createSession(group);

        Member member = data.member("Max", "Mustermann");

        var result = mockMvc.perform(post("/api/v1/sessions/{id}/register", session.getId())
                        .header("Authorization", bearer(adminToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "memberId": %d
                                }
                                """.formatted(member.getId())))
                .andExpect(status().isBadRequest());

        expectErrorCode(result, ErrorCode.MEMBER_NOT_ALLOWED_FOR_GROUP);
    }

    @Test
    void unregister_shouldReturnError_whenAfterSessionEnd() throws Exception {
        TrainingGroup group = data.group("Wettkampfgruppe");

        TrainingSession session = data.trainingSessionRepository().save(
                TrainingSession.builder()
                        .trainingGroup(group)
                        .title("Vergangenes Training")
                        .startTime(OffsetDateTime.parse("2025-01-01T10:00:00+02:00"))
                        .endTime(OffsetDateTime.parse("2025-01-01T12:00:00+02:00"))
                        .status(TrainingSessionStatus.OPEN)
                        .waitlistEnabled(true)
                        .build()
        );

        Member member = data.member("Max", "Mustermann");
        data.registration(session, member, RegistrationStatus.REGISTERED);

        var result = mockMvc.perform(post("/api/v1/sessions/{id}/unregister", session.getId())
                        .header("Authorization", bearer(adminToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                              "memberId": %d
                            }
                            """.formatted(member.getId())))
                .andExpect(status().isBadRequest());

        expectErrorCode(result, ErrorCode.UNREGISTER_AFTER_SESSION_END_NOT_ALLOWED);
    }

    @Test
    void unregister_shouldCancelRegistration() throws Exception {
        TrainingGroup group = data.group("Wettkampfgruppe");
        TrainingSession session = createSession(group);
        Member member = data.member("Max", "Mustermann");

        data.registration(session, member, RegistrationStatus.REGISTERED);

        mockMvc.perform(post("/api/v1/sessions/{id}/unregister", session.getId())
                        .header("Authorization", bearer(adminToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                              "memberId": %d
                            }
                            """.formatted(member.getId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELLED"))
                .andExpect(jsonPath("$.cancelledAt").isNotEmpty());
    }

    @Test
    void register_shouldReturnError_whenSessionNotFound() throws Exception {
        Member member = data.member("Max", "Mustermann");

        var result = mockMvc.perform(post("/api/v1/sessions/{id}/register", 999)
                        .header("Authorization", bearer(adminToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "memberId": %d
                                }
                                """.formatted(member.getId())))
                .andExpect(status().isNotFound());

        expectErrorCode(result, ErrorCode.TRAINING_SESSION_NOT_FOUND);
    }

    @Test
    void register_shouldReturnError_whenAlreadyRegistered() throws Exception {
        TrainingGroup group = data.group("Wettkampfgruppe");
        TrainingSession session = createSession(group);

        Member member = data.member("Max", "Mustermann");

        data.registration(session, member, RegistrationStatus.REGISTERED);
        data.memberGroupPermission(member, group);

        var result = mockMvc.perform(post("/api/v1/sessions/{id}/register", session.getId())
                        .header("Authorization", bearer(adminToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "memberId": %d
                                }
                                """.formatted(member.getId())))
                .andExpect(status().isConflict());

        expectErrorCode(result, ErrorCode.MEMBER_ALREADY_REGISTERED);
    }

    private TrainingSession createSession(TrainingGroup group) {
        return data.trainingSessionRepository().save(
                TrainingSession.builder()
                        .trainingGroup(group)
                        .title("Training")
                        .startTime(OffsetDateTime.parse("2026-05-06T17:00:00+02:00"))
                        .endTime(OffsetDateTime.parse("2026-05-06T19:00:00+02:00"))
                        .registrationDeadline(OffsetDateTime.parse("2026-05-06T12:00:00+02:00"))
                        .maxParticipants(20)
                        .waitlistEnabled(true)
                        .status(TrainingSessionStatus.OPEN)
                        .build()
        );
    }
}