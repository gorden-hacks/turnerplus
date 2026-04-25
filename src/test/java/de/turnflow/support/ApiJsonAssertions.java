package de.turnflow.support;

import de.turnflow.common.exception.ErrorCode;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

public final class ApiJsonAssertions {

    private ApiJsonAssertions() {
    }

    public static ResultActions expectErrorCode(ResultActions result, ErrorCode errorCode) throws Exception {
        return result
                .andExpect(jsonPath("$.errorCode").value(errorCode.getCode()))
                .andExpect(jsonPath("$.messageKey").value(errorCode.getMessageKey()))
                .andExpect(jsonPath("$.status").value(errorCode.getHttpStatus().value()));
    }
}