package com.example.bms.protocol.syslog;

import com.example.bms.common.domain.AlertStatus;
import com.example.bms.common.domain.Severity;
import com.example.bms.event.EventProcessingService;
import com.example.bms.event.EventSource;
import com.example.bms.event.IngestRequest;
import jakarta.annotation.PreDestroy;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.SmartLifecycle;
import org.springframework.stereotype.Component;

/**
 * 同一配置端口上的 UDP/TCP Syslog Receiver。
 *
 * <p>5514 是无管理员权限的本地默认值；生产部署可通过环境变量切换到 514，并由 NLB 暴露两种传输层协议。</p>
 */
@Component
public class SyslogReceiver implements SmartLifecycle {
    private static final Logger log = LoggerFactory.getLogger(SyslogReceiver.class);
    private final SyslogParser parser;
    private final EventProcessingService processor;
    private final boolean enabled;
    private final int port;
    private final AtomicBoolean running = new AtomicBoolean();
    private ExecutorService executor;
    private DatagramSocket udpSocket;
    private ServerSocket tcpSocket;

    public SyslogReceiver(SyslogParser parser, EventProcessingService processor,
                          @Value("${bms.protocol.syslog.enabled:true}") boolean enabled,
                          @Value("${bms.protocol.syslog.port:5514}") int port) {
        this.parser = parser;
        this.processor = processor;
        this.enabled = enabled;
        this.port = port;
    }

    @Override
    public void start() {
        if (!enabled || !running.compareAndSet(false, true)) return;
        executor = Executors.newVirtualThreadPerTaskExecutor();
        try {
            udpSocket = new DatagramSocket(port);
            tcpSocket = new ServerSocket(port);
            executor.submit(this::receiveUdp);
            executor.submit(this::acceptTcp);
            log.info("Syslog receiver started udp/tcp port={}", port);
        } catch (IOException ex) {
            running.set(false);
            closeSockets();
            throw new IllegalStateException("Syslog 端口绑定失败: " + port, ex);
        }
    }

    private void receiveUdp() {
        byte[] buffer = new byte[65_507];
        while (running.get()) {
            try {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                udpSocket.receive(packet);
                String raw = new String(packet.getData(), packet.getOffset(), packet.getLength(), StandardCharsets.UTF_8);
                handle(raw);
            } catch (IOException ex) {
                if (running.get()) log.error("UDP Syslog receive failed", ex);
            } catch (RuntimeException ex) {
                log.error("UDP Syslog processing failed type={}", ex.getClass().getSimpleName(), ex);
            }
        }
    }

    private void acceptTcp() {
        while (running.get()) {
            try {
                Socket socket = tcpSocket.accept();
                executor.submit(() -> readTcp(socket));
            } catch (IOException ex) {
                if (running.get()) log.error("TCP Syslog accept failed", ex);
            }
        }
    }

    private void readTcp(Socket socket) {
        try (socket; BufferedReader reader = new BufferedReader(
                new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) handle(line);
        } catch (IOException ex) {
            log.warn("TCP Syslog connection ended reason={}", ex.getClass().getSimpleName());
        }
    }

    private void handle(String raw) {
        ParsedSyslog parsed = parser.parse(raw);
        Severity severity = parsed.valid() ? mapSeverity(parsed.severityCode()) : Severity.WARNING;
        AlertStatus status = severity == Severity.CRITICAL ? AlertStatus.CRITICAL
                : severity == Severity.WARNING ? AlertStatus.WARNING : AlertStatus.NORMAL;
        processor.process(new IngestRequest(EventSource.SYSLOG, parsed.hostname(),
                parsed.valid() ? "syslog-" + parsed.facility() + "-" + normalizedKey(parsed.message()) : "syslog-parse-error",
                parsed.valid() ? parsed.message() : "Syslog形式エラー: " + parsed.error(), parsed.rawMessage(),
                severity, status, null, null, port, 0, parsed.facility(), parsed.format(), parsed.timestamp()));
    }

    private Severity mapSeverity(int value) {
        if (value >= 0 && value <= 3) return Severity.CRITICAL;
        if (value == 4) return Severity.WARNING;
        return Severity.INFO;
    }

    private String normalizedKey(String message) {
        return message.replaceAll("[^A-Za-z0-9]+", "-").replaceAll("(^-|-$)", "").toLowerCase();
    }

    @Override
    @PreDestroy
    public void stop() {
        if (!running.compareAndSet(true, false)) return;
        closeSockets();
        if (executor != null) executor.shutdownNow();
    }

    private void closeSockets() {
        if (udpSocket != null) udpSocket.close();
        try { if (tcpSocket != null) tcpSocket.close(); } catch (IOException ignored) { }
    }

    @Override public boolean isRunning() { return running.get(); }
    @Override public boolean isAutoStartup() { return true; }
    @Override public int getPhase() { return Integer.MAX_VALUE - 100; }
    public int getPort() { return port; }
    /** 接收器是否被当前组件角色启用；禁用是预期配置，不代表健康异常。 */
    public boolean isEnabled() { return enabled; }
}
