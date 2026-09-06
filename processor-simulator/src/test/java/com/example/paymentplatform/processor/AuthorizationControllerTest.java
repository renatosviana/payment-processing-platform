package com.example.paymentplatform.processor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AuthorizationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AuthorizationRegistry registry;

    @BeforeEach
    void clearRegistry() {
        registry.clear();
    }

    @Test
    void approvalCanBeInquired() throws Exception {
        mockMvc.perform(post("/authorizations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson("processor-http-001", "tok_visa_success")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.outcome").value("APPROVED"))
                .andExpect(jsonPath("$.requestCount").value(1))
                .andExpect(jsonPath("$.operationCount").value(1));

        mockMvc.perform(get("/authorizations/processor-http-001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.outcome").value("APPROVED"))
                .andExpect(jsonPath("$.requestCount").value(1))
                .andExpect(jsonPath("$.operationCount").value(1));
    }

    @Test
    void declineIsReturnedAsAStoredBusinessOutcome() throws Exception {
        mockMvc.perform(post("/authorizations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson("processor-http-002", "tok_visa_decline")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.outcome").value("DECLINED"));
    }

    @Test
    void replayAndConflictHaveTheRequiredHttpStatuses() throws Exception {
        String request = requestJson("processor-http-003", "tok_visa_success");

        mockMvc.perform(post("/authorizations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isCreated());
        mockMvc.perform(post("/authorizations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.requestCount").value(2))
                .andExpect(jsonPath("$.operationCount").value(1));
        mockMvc.perform(post("/authorizations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"processorKey":"processor-http-003","amountMinor":20000,"currency":"CAD","paymentMethodToken":"tok_visa_success"}
                                """))
                .andExpect(status().isConflict());
    }

    @Test
    void inquiryReturns404ForUnknownKey() throws Exception {
        mockMvc.perform(get("/authorizations/missing-key"))
                .andExpect(status().isNotFound());
    }

    private static String requestJson(String processorKey, String token) {
        return """
                {"processorKey":"%s","amountMinor":10000,"currency":"CAD","paymentMethodToken":"%s"}
                """.formatted(processorKey, token);
    }
}
