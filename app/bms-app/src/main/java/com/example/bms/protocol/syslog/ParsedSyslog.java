package com.example.bms.protocol.syslog;

import java.time.Instant;

/** Syslog Parser 的协议无关输出。 */
public record ParsedSyslog(
        boolean valid,
        String format,
        int priority,
        int facilityCode,
        String facility,
        int severityCode,
        String severity,
        Instant timestamp,
        String hostname,
        String message,
        String rawMessage,
        String error) {
}

