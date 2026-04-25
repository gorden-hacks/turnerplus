package de.turnflow.traininggroup;

import de.turnflow.common.exception.ErrorCode;
import de.turnflow.support.BaseApiIT;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static de.turnflow.support.ApiJsonAssertions.expectErrorCode;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class TrainingGroupControllerIT extends BaseApiIT {

    @Test
    void createTrainingGroup_shouldReturnConflict_whenNameAlreadyExists() throws Exception {
        data.group("Wettkampfgruppe");

        var result = mockMvc.perform(post("/api/v1/training-groups")
                        .header("Authorization", bearer(adminToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Wettkampfgruppe",
                                  "description": "Doppelt"
                                }
                                """))
                .andExpect(status().isConflict());

        expectErrorCode(result, ErrorCode.TRAINING_GROUP_ALREADY_EXISTS);
    }
}