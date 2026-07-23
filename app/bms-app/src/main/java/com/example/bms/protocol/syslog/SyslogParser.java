package com.example.bms.protocol.syslog;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.stereotype.Component;

/**
 * RFC 3164 与 RFC 5424 的基础字段 Parser。
 *
 * <p>Parser 不抛弃无法解析的原文，而是返回 valid=false；接收器将其保存为格式错误 Event，便于排障。</p>
 */
@Component
public class SyslogParser {
    private static final Pattern PRI = Pattern.compile("^<(\\d{1,3})>(.*)$", Pattern.DOTALL);
    private static final Pattern RFC5424 = Pattern.compile(
            "^(\\d+)\\s+(\\S+)\\s+(\\S+)\\s+(\\S+)\\s+(\\S+)\\s+(\\S+)\\s+(?:-|\\[[^]]*])\\s*(.*)$",
            Pattern.DOTALL);
    private static final Pattern RFC3164 = Pattern.compile(
            "^([A-Z][a-z]{2}\\s+\\d{1,2}\\s+\\d{2}:\\d{2}:\\d{2})\\s+(\\S+)\\s+(.*)$", Pattern.DOTALL);
    private static final DateTimeFormatter RFC3164_TIME =
            DateTimeFormatter.ofPattern("yyyy MMM d HH:mm:ss", Locale.ENGLISH);
    private static final List<String> FACILITIES = List.of("kernel", "user", "mail", "daemon", "auth", "syslog",
            "lpr", "news", "uucp", "clock", "authpriv", "ftp", "ntp", "audit", "alert", "clock2",
            "local0", "local1", "local2", "local3", "local4", "local5", "local6", "local7");
    private static final List<String> SEVERITIES = List.of("emergency", "alert", "critical", "error",
            "warning", "notice", "informational", "debug");

    /**
     * 解析一条 Syslog 原文。
     *
     * @param raw 收到的完整 UDP Datagram 或 TCP 行
     * @return 包含 Facility/Severity/时间/hostname/message 的结果
     */
    public ParsedSyslog parse(String raw) {
        if (raw == null || raw.isBlank()) return invalid(raw, "消息为空");
        Matcher priMatcher = PRI.matcher(raw.strip());
        if (!priMatcher.matches()) return invalid(raw, "缺少或无法识别 PRI");
        int priority;
        try {
            priority = Integer.parseInt(priMatcher.group(1));
        } catch (NumberFormatException ex) {
            return invalid(raw, "PRI 不是数字");
        }
        if (priority < 0 || priority > 191) return invalid(raw, "PRI 超出 0..191");
        String body = priMatcher.group(2);
        int facilityCode = priority / 8;
        int severityCode = priority % 8;

        Matcher modern = RFC5424.matcher(body);
        if (modern.matches()) {
            try {
                Instant timestamp = "-".equals(modern.group(2)) ? Instant.now()
                        : OffsetDateTime.parse(modern.group(2)).toInstant();
                return valid("RFC5424", priority, facilityCode, severityCode, timestamp,
                        modern.group(3), modern.group(7), raw);
            } catch (DateTimeParseException ex) {
                return invalid(raw, "RFC5424 timestamp 无法解析");
            }
        }

        Matcher legacy = RFC3164.matcher(body);
        if (legacy.matches()) {
            try {
                int year = OffsetDateTime.now(ZoneOffset.UTC).getYear();
                Instant timestamp = LocalDateTime.parse(year + " " + legacy.group(1), RFC3164_TIME)
                        .toInstant(ZoneOffset.UTC);
                return valid("RFC3164", priority, facilityCode, severityCode, timestamp,
                        legacy.group(2), legacy.group(3), raw);
            } catch (DateTimeParseException ex) {
                return invalid(raw, "RFC3164 timestamp 无法解析");
            }
        }
        return invalid(raw, "不符合 RFC3164/RFC5424 基础格式");
    }

    private ParsedSyslog valid(String format, int priority, int facilityCode, int severityCode,
                               Instant timestamp, String hostname, String message, String raw) {
        String facility = facilityCode < FACILITIES.size() ? FACILITIES.get(facilityCode) : "unknown";
        return new ParsedSyslog(true, format, priority, facilityCode, facility, severityCode,
                SEVERITIES.get(severityCode), timestamp, hostname, message, raw, null);
    }

    private ParsedSyslog invalid(String raw, String error) {
        return new ParsedSyslog(false, "UNKNOWN", -1, -1, "unknown", -1, "unknown",
                Instant.now(), "unknown", error, raw == null ? "" : raw, error);
    }
}

