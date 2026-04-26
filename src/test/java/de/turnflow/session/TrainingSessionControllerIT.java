package de.turnflow.session;

import de.turnflow.common.exception.ErrorCode;
import de.turnflow.member.entity.Member;
import de.turnflow.registration.entity.RegistrationStatus;
import de.turnflow.session.entity.TrainingSession;
import de.turnflow.session.entity.TrainingSessionStatus;
import de.turnflow.support.BaseApiIT;
import de.turnflow.traininggroup.entity.TrainingGroup;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static de.turnflow.support.ApiJsonAssertions.expectErrorCode;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class TrainingSessionControllerIT extends BaseApiIT {

    @Test
    void getFiltered_shouldReturnPagedTrainingSessions() throws Exception {
        TrainingGroup group = data.group("Wettkampfgruppe");

        data.session(group, "Training 1", "2026-05-06T17:00:00+02:00", TrainingSessionStatus.OPEN);
        data.session(group, "Training 2", "2026-05-07T17:00:00+02:00", TrainingSessionStatus.OPEN);
        data.session(group, "Training 3", "2026-05-08T17:00:00+02:00", TrainingSessionStatus.OPEN);

        mockMvc.perform(get("/api/v1/training-sessions")
                        .header("Authorization", bearer(adminToken))
                        .param("page", "0")
                        .param("size", "2")
                        .param("sort", "startTime,asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(2))
                .andExpect(jsonPath("$.totalElements").value(3))
                .andExpect(jsonPath("$.totalPages").value(2))
                .andExpect(jsonPath("$.first").value(true))
                .andExpect(jsonPath("$.last").value(false));
    }

    @Test
    void getFiltered_shouldFilterByGroupAndStatus() throws Exception {
        TrainingGroup groupA = data.group("Wettkampfgruppe");
        TrainingGroup groupB = data.group("Anfängergruppe");

        data.session(groupA, "Offen A", "2026-05-06T17:00:00+02:00", TrainingSessionStatus.OPEN);
        data.session(groupA, "Abgesagt A", "2026-05-07T17:00:00+02:00", TrainingSessionStatus.CANCELLED);
        data.session(groupB, "Offen B", "2026-05-08T17:00:00+02:00", TrainingSessionStatus.OPEN);

        mockMvc.perform(get("/api/v1/training-sessions")
                        .header("Authorization", bearer(adminToken))
                        .param("groupId", groupA.getId().toString())
                        .param("status", "OPEN")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].title").value("Offen A"))
                .andExpect(jsonPath("$.content[0].trainingGroupId").value(groupA.getId()))
                .andExpect(jsonPath("$.content[0].status").value("OPEN"));
    }

    @Test
    void getFiltered_shouldIncludeRegistrationCounts() throws Exception {
        TrainingGroup group = data.group("Wettkampfgruppe");
        TrainingSession session = data.session(group, "Training", "2026-05-06T17:00:00+02:00", TrainingSessionStatus.OPEN);

        Member member1 = data.member("Max", "Muster");
        Member member2 = data.member("Lisa", "Turner");

        data.registration(session, member1, RegistrationStatus.REGISTERED);
        data.registration(session, member2, RegistrationStatus.WAITLIST);

        mockMvc.perform(get("/api/v1/training-sessions")
                        .header("Authorization", bearer(adminToken))
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].registeredCount").value(1))
                .andExpect(jsonPath("$.content[0].waitlistCount").value(1));
    }

    @Test
    void getById_shouldReturnNotFoundErrorCode_whenSessionDoesNotExist() throws Exception {
        var result = mockMvc.perform(get("/api/v1/training-sessions/999")
                        .header("Authorization", bearer(adminToken)))
                .andExpect(status().isNotFound());

        expectErrorCode(result, ErrorCode.TRAINING_SESSION_NOT_FOUND);
    }

    @Test
    void create_shouldReturnNotFoundErrorCode_whenGroupDoesNotExist() throws Exception {
        var result = mockMvc.perform(post("/api/v1/training-sessions")
                        .header("Authorization", bearer(adminToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "trainingGroupId": 999,
                                  "title": "Mittwochstraining",
                                  "location": "Turnhalle Nord",
                                  "startTime": "2026-05-06T17:00:00+02:00",
                                  "endTime": "2026-05-06T19:00:00+02:00",
                                  "registrationDeadline": "2026-05-06T12:00:00+02:00",
                                  "maxParticipants": 20,
                                  "waitlistEnabled": true,
                                  "status": "OPEN"
                                }
                                """))
                .andExpect(status().isNotFound());

        expectErrorCode(result, ErrorCode.TRAINING_GROUP_NOT_FOUND);
    }

    @Test
    void create_shouldReturnValidationError_whenEndTimeBeforeStartTime() throws Exception {
        TrainingGroup group = data.group("Wettkampfgruppe");

        mockMvc.perform(post("/api/v1/training-sessions")
                        .header("Authorization", bearer(adminToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "trainingGroupId": %d,
                                  "title": "Mittwochstraining",
                                  "startTime": "2026-05-06T19:00:00+02:00",
                                  "endTime": "2026-05-06T17:00:00+02:00",
                                  "status": "OPEN"
                                }
                                """.formatted(group.getId())))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.messageKey").value("error.validation"));
    }

    @Test
    void update_shouldReturnBusinessErrorCode_whenPartialUpdateMakesInvalidTimeRange() throws Exception {
        TrainingGroup group = data.group("Wettkampfgruppe");
        TrainingSession session = data.session(group, "Training", "2026-05-06T17:00:00+02:00", TrainingSessionStatus.OPEN);

        var result = mockMvc.perform(put("/api/v1/training-sessions/{id}", session.getId())
                        .header("Authorization", bearer(adminToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "endTime": "2026-05-06T16:00:00+02:00"
                                }
                                """))
                .andExpect(status().isBadRequest());

        expectErrorCode(result, ErrorCode.TRAINING_SESSION_INVALID_TIME_RANGE);
    }

    @Test
    void calendar_shouldReturnSessionsInRange() throws Exception {
        TrainingGroup group = data.group("Wettkampfgruppe");

        data.session(group, "In Range", "2026-05-06T17:00:00+02:00", TrainingSessionStatus.OPEN);
        data.session(group, "Out Range", "2026-06-06T17:00:00+02:00", TrainingSessionStatus.OPEN);

        mockMvc.perform(get("/api/v1/training-sessions/calendar")
                        .header("Authorization", bearer(adminToken))
                        .param("from", "2026-05-01T00:00:00+02:00")
                        .param("to", "2026-05-31T23:59:59+02:00")
                        .param("groupId", group.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title").value("In Range"));
    }

    @Test
    void isoWeekCalendar_shouldReturnDaysGroupedByDate() throws Exception {
        TrainingGroup group = data.group("Wettkampfgruppe");

        data.session(group, "Mittwochstraining", "2026-05-06T17:00:00+02:00", TrainingSessionStatus.OPEN);

        mockMvc.perform(get("/api/v1/training-sessions/calendar/iso-week")
                        .header("Authorization", bearer(adminToken))
                        .param("year", "2026")
                        .param("week", "19")
                        .param("groupId", group.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.weekStart").value("2026-05-04"))
                .andExpect(jsonPath("$.weekEnd").value("2026-05-10"))
                .andExpect(jsonPath("$.days", hasSize(7)))
                .andExpect(jsonPath("$.days[2].date").value("2026-05-06"))
                .andExpect(jsonPath("$.days[2].sessions[0].title").value("Mittwochstraining"));
    }

}