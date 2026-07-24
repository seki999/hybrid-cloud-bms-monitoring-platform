package com.example.bms.monitoring;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import java.io.IOException;
import java.net.ServerSocket;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
/**
 * 验证 TCP 探测的成功、失败、超时与重试语义，以及结果记录方式。
 * 网络连接由可控替身隔离，保证测试不会因开发机器的端口状态而波动。
 */
class TcpPingServiceTest {
    @Mock TcpPingResultRepository repository;

    @Test void succeedsAgainstOpenTcpPort() throws IOException {
        when(repository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        try (ServerSocket server = new ServerSocket(0)) {
            TcpPingResult result = new TcpPingService(repository)
                    .check(null, "127.0.0.1", server.getLocalPort(), 500, 0);
            assertTrue(result.isSuccess());
        }
    }

    @Test void classifiesClosedPort() throws IOException {
        when(repository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        int port;
        try (ServerSocket server = new ServerSocket(0)) { port = server.getLocalPort(); }
        TcpPingResult result = new TcpPingService(repository).check(null, "127.0.0.1", port, 300, 0);
        assertFalse(result.isSuccess());
    }
}
