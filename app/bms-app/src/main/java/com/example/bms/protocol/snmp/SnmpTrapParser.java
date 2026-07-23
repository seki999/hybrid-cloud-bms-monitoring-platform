package com.example.bms.protocol.snmp;

import com.example.bms.common.domain.AlertStatus;
import com.example.bms.common.domain.Severity;
import java.time.Instant;
import java.util.stream.Collectors;
import org.snmp4j.PDU;
import org.snmp4j.smi.VariableBinding;
import org.springframework.stereotype.Component;

/** 解析 SNMP4J PDU；linkDown/linkUp OID 决定故障或恢复，其余 Trap 作为 WARNING。 */
@Component
public class SnmpTrapParser {
    private static final String LINK_DOWN = "1.3.6.1.6.3.1.1.5.3";
    private static final String LINK_UP = "1.3.6.1.6.3.1.1.5.4";

    /** @param host 去除 UDP 端口后的发送源地址 @param pdu SNMPv2c PDU */
    public ParsedSnmpTrap parse(String host, PDU pdu) {
        String raw = pdu.getVariableBindings().stream().map(VariableBinding::toString)
                .collect(Collectors.joining("; "));
        String trapOid = pdu.getVariableBindings().stream()
                .filter(binding -> binding.getOid().toDottedString().equals("1.3.6.1.6.3.1.1.4.1.0"))
                .map(binding -> binding.getVariable().toString()).findFirst()
                .orElseGet(() -> pdu.size() == 0 ? "unknown" : pdu.get(0).getOid().toDottedString());
        if (trapOid.equals(LINK_UP)) {
            return new ParsedSnmpTrap(host, "snmp-" + LINK_DOWN, "SNMP Trap: linkUp", raw,
                    Severity.INFO, AlertStatus.RECOVERED, Instant.now());
        }
        if (trapOid.equals(LINK_DOWN)) {
            return new ParsedSnmpTrap(host, "snmp-" + LINK_DOWN, "SNMP Trap: linkDown", raw,
                    Severity.CRITICAL, AlertStatus.CRITICAL, Instant.now());
        }
        return new ParsedSnmpTrap(host, "snmp-" + trapOid, "SNMP Trap: " + trapOid, raw,
                Severity.WARNING, AlertStatus.WARNING, Instant.now());
    }
}

