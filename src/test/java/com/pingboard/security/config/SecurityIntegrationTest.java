package com.pingboard.security.config;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class SecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void healthEndpointIsPublic() throws Exception {
        mockMvc.perform(get("/actuator/health"))
                .andExpect(status().isOk());
    }

    @Test
    void monitorApiRequiresAuthentication() throws Exception {
        mockMvc.perform(get("/api/monitors"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void monitorApiAcceptsConfiguredOperatorCredentials() throws Exception {
        mockMvc.perform(get("/api/monitors").with(httpBasic("operator", "pingboard123!")))
                .andExpect(status().isOk());
    }
}
