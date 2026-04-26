package de.turnflow.security;

import de.turnflow.support.BaseApiIT;
import de.turnflow.traininggroup.entity.TrainingGroup;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class SecurityAccessIT extends BaseApiIT {

    @Test
    void getTrainingSessions_shouldReturnUnauthorized_withoutToken() throws Exception {
        mockMvc.perform(get("/api/v1/training-sessions"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getTrainingSessions_shouldAllowMember() throws Exception {
        String memberToken = auth.loginAsMember();

        mockMvc.perform(get("/api/v1/training-sessions")
                        .header("Authorization", bearer(memberToken)))
                .andExpect(status().isOk());
    }

    @Test
    void createTrainingSession_shouldForbidMember() throws Exception {
        String memberToken = auth.loginAsMember();
        TrainingGroup group = data.group("Wettkampfgruppe");

        mockMvc.perform(post("/api/v1/training-sessions")
                        .header("Authorization", bearer(memberToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "trainingGroupId": %d,
                                  "title": "Mittwochstraining",
                                  "startTime": "2026-05-06T17:00:00+02:00",
                                  "endTime": "2026-05-06T19:00:00+02:00",
                                  "status": "OPEN"
                                }
                                """.formatted(group.getId())))
                .andExpect(status().isForbidden());
    }

    @Test
    void createTrainingSession_shouldAllowAdmin() throws Exception {
        TrainingGroup group = data.group("Wettkampfgruppe");

        mockMvc.perform(post("/api/v1/training-sessions")
                        .header("Authorization", bearer(adminToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "trainingGroupId": %d,
                                  "title": "Mittwochstraining",
                                  "startTime": "2026-05-06T17:00:00+02:00",
                                  "endTime": "2026-05-06T19:00:00+02:00",
                                  "status": "OPEN"
                                }
                                """.formatted(group.getId())))
                .andExpect(status().isOk());
    }

    @Test
    void createTrainingGroup_shouldForbidTrainer() throws Exception {
        String trainerToken = auth.loginAsTrainer();

        mockMvc.perform(post("/api/v1/training-groups")
                        .header("Authorization", bearer(trainerToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Neue Gruppe",
                                  "description": "Test"
                                }
                                """))
                .andExpect(status().isForbidden());
    }

    @Test
    void unregister_shouldRequireAuthentication() throws Exception {
        mockMvc.perform(post("/api/v1/sessions/1/unregister")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "memberId": 1
                                }
                                """))
                .andExpect(status().isUnauthorized());
    }
}