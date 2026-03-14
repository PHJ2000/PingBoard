package com.pingboard.dev.api;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class DevToolsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void sentryTestEndpointReturnsServerErrorForAuthenticatedUser() throws Exception {
        assertThatThrownBy(() -> mockMvc.perform(post("/api/dev/sentry-test").with(httpBasic("operator", "pingboard123!"))))
                .hasRootCauseInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Request processing failed");
    }
}
