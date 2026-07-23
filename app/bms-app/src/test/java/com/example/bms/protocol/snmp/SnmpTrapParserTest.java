package com.example.bms.protocol.snmp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import com.example.bms.common.domain.AlertStatus;
import org.junit.jupiter.api.Test;
import org.snmp4j.PDU;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.VariableBinding;

class SnmpTrapParserTest {
    private final SnmpTrapParser parser = new SnmpTrapParser();

    @Test void linkDownCreatesCriticalState() {
        PDU pdu = new PDU();
        pdu.add(new VariableBinding(new OID("1.3.6.1.6.3.1.1.4.1.0"), new OID("1.3.6.1.6.3.1.1.5.3")));
        ParsedSnmpTrap result = parser.parse("10.30.1.11", pdu);
        assertEquals(AlertStatus.CRITICAL, result.status());
        assertEquals("SNMP Trap: linkDown", result.message());
    }

    @Test void linkUpUsesSameAlertKeyForRecovery() {
        PDU pdu = new PDU();
        pdu.add(new VariableBinding(new OID("1.3.6.1.6.3.1.1.4.1.0"), new OID("1.3.6.1.6.3.1.1.5.4")));
        ParsedSnmpTrap result = parser.parse("10.30.1.11", pdu);
        assertEquals(AlertStatus.RECOVERED, result.status());
        assertEquals("snmp-1.3.6.1.6.3.1.1.5.3", result.eventKey());
    }
}

