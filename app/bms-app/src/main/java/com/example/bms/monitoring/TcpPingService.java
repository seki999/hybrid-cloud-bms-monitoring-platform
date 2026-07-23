package com.example.bms.monitoring;

import com.example.bms.device.Device;
import java.io.IOException;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.time.Instant;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 使用 Java Socket 执行 TCP Ping，而不是 ICMP Echo。
 *
 * <p>每次尝试创建新 Socket，避免失败连接残留状态；只记录错误类型与脱敏消息，不记录凭据。</p>
 */
@Service
public class TcpPingService {
    private final TcpPingResultRepository repository;

    public TcpPingService(TcpPingResultRepository repository) { this.repository = repository; }

    /**
     * 对 host:port 执行有限次数连接。
     *
     * @param device 可为空的设备主数据
     * @param host DNS 或 IP
     * @param port TCP 端口
     * @param timeoutMillis 单次超时
     * @param maxRetries 失败后的最大重试次数
     * @return 已保存的结构化结果
     */
    @Transactional
    public TcpPingResult check(Device device, String host, int port, int timeoutMillis, int maxRetries) {
        long start = System.nanoTime();
        String errorType = null;
        String errorMessage = null;
        int attempts = 0;
        boolean success = false;

        for (int attempt = 0; attempt <= Math.max(maxRetries, 0); attempt++) {
            attempts = attempt;
            try (Socket socket = new Socket()) {
                socket.connect(new InetSocketAddress(host, port), timeoutMillis);
                success = true;
                errorType = null;
                errorMessage = null;
                break;
            } catch (UnknownHostException ex) {
                errorType = "DNS_ERROR";
                errorMessage = ex.getMessage();
                break; // 同一轮重试中 DNS 不会自行修复，立即结束可降低无效等待。
            } catch (SocketTimeoutException ex) {
                errorType = "TIMEOUT";
                errorMessage = ex.getMessage();
            } catch (ConnectException ex) {
                errorType = "CONNECTION_REFUSED";
                errorMessage = ex.getMessage();
            } catch (IOException ex) {
                errorType = "IO_ERROR";
                errorMessage = ex.getClass().getSimpleName() + ": " + ex.getMessage();
            }
        }

        long elapsedMillis = Math.max(0, (System.nanoTime() - start) / 1_000_000);
        return repository.save(new TcpPingResult(device, host, port, success, elapsedMillis, errorType,
                errorMessage, Instant.now(), attempts));
    }
}

