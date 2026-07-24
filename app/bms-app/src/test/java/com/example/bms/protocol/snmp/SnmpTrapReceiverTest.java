package com.example.bms.protocol.snmp;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

import com.example.bms.event.EventProcessingService;
import java.net.DatagramSocket;
import org.junit.jupiter.api.Test;
import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.Integer32;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.TimeTicks;
import org.snmp4j.smi.UdpAddress;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultUdpTransportMapping;

/**
 * 在 IPv4 回环地址上验证 Trap 接收器的真实 UDP 收发与生命周期管理。
 * 使用动态端口避免与本机 SNMP 服务冲突，并确保测试不会依赖外部网络设备。
 */
class SnmpTrapReceiverTest {

    @Test
    void receivesTrapSentToIpv4Loopback() throws Exception {
        int port;
        try (DatagramSocket socket = new DatagramSocket(0)) {
            port = socket.getLocalPort();
        }

        EventProcessingService processor = mock(EventProcessingService.class);
        SnmpTrapReceiver receiver = new SnmpTrapReceiver(
                new SnmpTrapParser(), processor, true, port);
        receiver.start();
        try (DefaultUdpTransportMapping transport = new DefaultUdpTransportMapping();
             Snmp snmp = new Snmp(transport)) {
            transport.listen();
            CommunityTarget<UdpAddress> target = new CommunityTarget<>();
            target.setCommunity(new OctetString("test-community"));
            target.setVersion(SnmpConstants.version2c);
            target.setAddress(new UdpAddress("127.0.0.1/" + port));

            PDU pdu = new PDU();
            pdu.setType(PDU.NOTIFICATION);
            pdu.add(new VariableBinding(
                    new OID("1.3.6.1.2.1.1.3.0"), new TimeTicks(123)));
            pdu.add(new VariableBinding(
                    new OID("1.3.6.1.6.3.1.1.4.1.0"),
                    new OID("1.3.6.1.6.3.1.1.5.3")));
            pdu.add(new VariableBinding(
                    new OID("1.3.6.1.2.1.2.2.1.1.1"), new Integer32(1)));

            snmp.send(pdu, target);
            verify(processor, timeout(2_000)).process(any());
        } finally {
            receiver.stop();
        }
    }
}
