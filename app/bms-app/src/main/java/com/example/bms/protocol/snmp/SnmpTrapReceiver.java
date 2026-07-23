package com.example.bms.protocol.snmp;

import com.example.bms.event.EventProcessingService;
import com.example.bms.event.EventSource;
import com.example.bms.event.IngestRequest;
import jakarta.annotation.PreDestroy;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snmp4j.CommandResponderEvent;
import org.snmp4j.Snmp;
import org.snmp4j.smi.UdpAddress;
import org.snmp4j.transport.DefaultUdpTransportMapping;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.SmartLifecycle;
import org.springframework.stereotype.Component;

/** SNMP4J v2c Trap Receiver；本地 1162，生产由 NLB UDP 162 转发。 */
@Component
public class SnmpTrapReceiver implements SmartLifecycle {
    private static final Logger log = LoggerFactory.getLogger(SnmpTrapReceiver.class);
    private final SnmpTrapParser parser;
    private final EventProcessingService processor;
    private final boolean enabled;
    private final int port;
    private final AtomicBoolean running = new AtomicBoolean();
    private Snmp snmp;

    public SnmpTrapReceiver(SnmpTrapParser parser, EventProcessingService processor,
                            @Value("${bms.protocol.snmp.enabled:true}") boolean enabled,
                            @Value("${bms.protocol.snmp.trap-port:1162}") int port) {
        this.parser = parser;
        this.processor = processor;
        this.enabled = enabled;
        this.port = port;
    }

    @Override
    public void start() {
        if (!enabled || !running.compareAndSet(false, true)) return;
        try {
            // 仅传入端口时，SNMP4J 可能选择主机的某个具体网卡地址，导致 localhost
            // 模拟器的数据包无法到达。显式监听 IPv4 通配地址，使本机、Docker 和 NLB
            // 转发到 Pod/容器地址的 Trap 都进入同一个接收器。
            DefaultUdpTransportMapping transport =
                    new DefaultUdpTransportMapping(new UdpAddress("0.0.0.0/" + port));
            snmp = new Snmp(transport);
            snmp.addCommandResponder(this::onTrap);
            transport.listen();
            log.info("SNMP Trap receiver started udp port={}", port);
        } catch (IOException ex) {
            running.set(false);
            throw new IllegalStateException("SNMP Trap 端口绑定失败: " + port, ex);
        }
    }

    private void onTrap(CommandResponderEvent event) {
        if (event.getPDU() == null) return;
        try {
            String peer = String.valueOf(event.getPeerAddress());
            String host = peer.contains("/") ? peer.substring(0, peer.indexOf('/')) : peer;
            ParsedSnmpTrap parsed = parser.parse(host, event.getPDU());
            processor.process(new IngestRequest(EventSource.SNMP_TRAP, parsed.host(), parsed.eventKey(),
                    parsed.message(), parsed.raw(), parsed.severity(), parsed.status(), null, null, port, 0,
                    null, "SNMPv2c", parsed.occurredAt()));
        } catch (RuntimeException ex) {
            log.error("SNMP Trap processing failed type={}", ex.getClass().getSimpleName(), ex);
        }
    }

    @Override
    @PreDestroy
    public void stop() {
        if (!running.compareAndSet(true, false)) return;
        if (snmp != null) {
            try { snmp.close(); } catch (IOException ex) { log.warn("SNMP receiver close failed", ex); }
        }
    }

    @Override public boolean isRunning() { return running.get(); }
    @Override public boolean isAutoStartup() { return true; }
    @Override public int getPhase() { return Integer.MAX_VALUE - 90; }
    public int getPort() { return port; }
    /** 接收器是否被当前组件角色启用；禁用是预期配置，不代表健康异常。 */
    public boolean isEnabled() { return enabled; }
}
