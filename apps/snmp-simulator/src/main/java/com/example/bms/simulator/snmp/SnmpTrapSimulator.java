package com.example.bms.simulator.snmp;

import java.io.IOException;
import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.TimeTicks;
import org.snmp4j.smi.UdpAddress;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultUdpTransportMapping;

/**
 * SNMPv2c linkDown/linkUp Trap 发送器。
 *
 * <p>Community 只从命令参数或环境变量取得。默认值是明确的本地占位值，不得用于生产设备。</p>
 */
public final class SnmpTrapSimulator {
    private static final OID SYS_UP_TIME = new OID("1.3.6.1.2.1.1.3.0");
    private static final OID TRAP_OID = new OID("1.3.6.1.6.3.1.1.4.1.0");
    private static final OID LINK_DOWN = new OID("1.3.6.1.6.3.1.1.5.3");
    private static final OID LINK_UP = new OID("1.3.6.1.6.3.1.1.5.4");

    private SnmpTrapSimulator() { }

    public static void main(String[] args) throws IOException {
        String host = args.length > 0 ? args[0] : "localhost";
        int port = args.length > 1 ? Integer.parseInt(args[1]) : 1162;
        String community = args.length > 2 ? args[2]
                : System.getenv().getOrDefault("SNMP_COMMUNITY", "change-me-local-community");
        boolean recovery = args.length > 3 && args[3].equalsIgnoreCase("linkUp");

        try (DefaultUdpTransportMapping transport = new DefaultUdpTransportMapping();
             Snmp snmp = new Snmp(transport)) {
            transport.listen();
            CommunityTarget<UdpAddress> target = new CommunityTarget<>();
            target.setCommunity(new OctetString(community));
            target.setVersion(SnmpConstants.version2c);
            target.setAddress(new UdpAddress(host + "/" + port));
            target.setRetries(1);
            target.setTimeout(1500);

            PDU pdu = new PDU();
            pdu.setType(PDU.NOTIFICATION);
            pdu.setRequestID(new org.snmp4j.smi.Integer32((int) (System.nanoTime() & 0x7fffffff)));
            pdu.add(new VariableBinding(SYS_UP_TIME, new TimeTicks(12_345)));
            pdu.add(new VariableBinding(TRAP_OID, recovery ? LINK_UP : LINK_DOWN));
            pdu.add(new VariableBinding(new OID("1.3.6.1.2.1.2.2.1.1.1"), new org.snmp4j.smi.Integer32(1)));
            snmp.send(pdu, target);
        }
        System.out.printf("sent SNMPv2c %s to %s:%d%n", recovery ? "linkUp" : "linkDown", host, port);
    }
}

