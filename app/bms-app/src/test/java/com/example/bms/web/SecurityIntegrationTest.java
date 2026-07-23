package com.example.bms.web;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SecurityIntegrationTest {
    @Autowired MockMvc mvc;

    @Test void anonymousIsRedirectedToLogin() throws Exception {
        mvc.perform(get("/dashboard")).andExpect(status().is3xxRedirection());
    }

    @Test void viewerCannotOpenAdminUserPage() throws Exception {
        mvc.perform(get("/users").with(user("viewer").roles("VIEWER"))).andExpect(status().isForbidden());
    }

    @Test void adminCanOpenAdminUserPage() throws Exception {
        mvc.perform(get("/users").with(user("admin").roles("ADMIN"))).andExpect(status().isOk());
    }
}

