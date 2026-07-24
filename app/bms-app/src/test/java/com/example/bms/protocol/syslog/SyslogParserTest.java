package com.example.bms.protocol.syslog;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

/**
 * 覆盖 RFC 3164、RFC 5424 与异常输入的 Syslog 解析行为。
 * 固定报文样本用于保护优先级、时间戳、主机名和消息正文的兼容性。
 */
class SyslogParserTest {
    private final SyslogParser parser = new SyslogParser();

    @Test void parsesRfc3164FacilityAndSeverity() {
        ParsedSyslog result = parser.parse("<132>Jul 23 10:25:00 10.20.1.11 interface ge-0/0/0 down");
        assertTrue(result.valid());
        assertEquals("RFC3164", result.format());
        assertEquals("local0", result.facility());
        assertEquals("warning", result.severity());
        assertEquals("10.20.1.11", result.hostname());
    }

    @Test void parsesRfc5424TimestampAndMessage() {
        ParsedSyslog result = parser.parse("<14>1 2026-07-23T10:25:00+09:00 10.20.1.11 app 1 ID47 - service ready");
        assertTrue(result.valid());
        assertEquals("RFC5424", result.format());
        assertEquals("service ready", result.message());
    }

    @Test void preservesMalformedRawMessage() {
        ParsedSyslog result = parser.parse("not-a-syslog-message");
        assertFalse(result.valid());
        assertEquals("not-a-syslog-message", result.rawMessage());
    }
}
