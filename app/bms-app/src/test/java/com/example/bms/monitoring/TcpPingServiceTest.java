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

