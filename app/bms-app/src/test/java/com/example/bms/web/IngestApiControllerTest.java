package com.example.bms.web;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import com.example.bms.event.EventProcessingService;
import com.example.bms.infrastructure.DemoDataService;
import com.example.bms.monitoring.TcpPingService;
import com.example.bms.protocol.snmp.SnmpQueryClient;
import com.example.bms.security.ApiKeyService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.test.context.support.WithMockUser;

@WebMvcTest(IngestApiController.class)
@WithMockUser
class IngestApiControllerTest {
    @Autowired MockMvc mvc;
    @MockitoBean ApiKeyService keys;
    @MockitoBean EventProcessingService events;
    @MockitoBean DemoDataService demo;
    @MockitoBean TcpPingService tcp;
    @MockitoBean SnmpQueryClient snmp;

    @Test void rejectsMissingApiKey() throws Exception {
        when(keys.isValid(any())).thenReturn(false);
        mvc.perform(post("/api/v1/ingest/events").with(csrf()).contentType(MediaType.APPLICATION_JSON)
                .content("{\"source\":\"SYSLOG\",\"host\":\"10.0.0.1\",\"eventKey\":\"x\",\"message\":\"m\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test void validatesRequiredFields() throws Exception {
        when(keys.isValid("key")).thenReturn(true);
        mvc.perform(post("/api/v1/ingest/events").with(csrf()).header("X-BMS-API-Key", "key")
                .contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isBadRequest());
    }
}
