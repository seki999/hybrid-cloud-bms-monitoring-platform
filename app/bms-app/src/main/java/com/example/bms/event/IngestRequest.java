package com.example.bms.event;

import com.example.bms.common.domain.AlertStatus;
import com.example.bms.common.domain.Severity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import java.time.Instant;

/**
 * AWS Lambda、协议接收器和本地模拟器共同使用的标准化输入 DTO。
 *
 * @param source 事件来源协议
 * @param host 设备 hostname 或 IP，用于主数据匹配
 * @param eventKey 稳定的故障键，用于聚合 Alert
 * @param message 标准化后的可读消息
 * @param rawMessage 未丢失的协议原文
 * @param severity 标准化级别；协议 Adapter 可先计算
 * @param status 明确的故障/恢复状态；为空时由数值和成功标志判定
 * @param metricValue SNMP/TCP 等数值
 * @param success 主动检查结果
 * @param port TCP 目标端口
 * @param retryCount 已执行重试次数
 * @param facility Syslog Facility
 * @param protocol 协议版本，例如 RFC5424 或 SNMPv2c
 * @param occurredAt 源端时间；为空时使用 OCI 接收时间
 */
public record IngestRequest(
        @NotNull EventSource source,
        @NotBlank @Size(max = 255) String host,
        @NotBlank @Size(max = 180) String eventKey,
        @NotBlank @Size(max = 1000) String message,
        String rawMessage,
        Severity severity,
        AlertStatus status,
        Double metricValue,
        Boolean success,
        Integer port,
        @PositiveOrZero Integer retryCount,
        String facility,
        String protocol,
        Instant occurredAt) {
}

