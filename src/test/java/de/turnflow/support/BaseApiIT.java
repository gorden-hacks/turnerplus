package de.turnflow.support;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(TestConfig.class)
public abstract class BaseApiIT {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected TestAuthHelper auth;

    @Autowired
    protected TestDataFactory data;

    protected String adminToken;

    @BeforeEach
    void baseSetUp() throws Exception {
        data.cleanDatabase();
        data.ensureRoles();
        adminToken = auth.loginAsAdmin();
    }

    protected String bearer(String token) {
        return "Bearer " + token;
    }
}