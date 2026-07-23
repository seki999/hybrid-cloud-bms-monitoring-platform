package com.example.bms.protocol.snmp;

import com.example.bms.common.domain.AlertStatus;
import com.example.bms.common.domain.Severity;
import java.time.Instant;

/** SNMP Trap Parser 输出。 */
public record ParsedSnmpTrap(String host, String eventKey, String message, String raw,
                             Severity severity, AlertStatus status, Instant occurredAt) { }

