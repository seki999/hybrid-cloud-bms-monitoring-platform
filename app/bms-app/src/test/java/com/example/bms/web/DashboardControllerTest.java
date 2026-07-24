package com.example.bms.web;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(DashboardController.class)
/**
 * 验证仪表盘路由能够组装视图模型并返回预期模板。
 * 该层测试关注控制器与 Thymeleaf 的契约，不重复测试统计服务内部实现。
 */
class DashboardControllerTest {
    @Autowired MockMvc mvc;
    @MockitoBean DashboardService service;

    @Test void authenticatedViewerCanOpenDashboard() throws Exception {
        when(service.getDashboard()).thenReturn(new DashboardView(10, 7, 2, 1, 3, 30, 20, 85,
                List.of(), List.of(), List.of(1L, 2L)));
        mvc.perform(get("/dashboard").with(user("viewer").roles("VIEWER")))
                .andExpect(status().isOk())
                .andExpect(view().name("dashboard"))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("href=\"/css/app.css\"")));
    }
}
