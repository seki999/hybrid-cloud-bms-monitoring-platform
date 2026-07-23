package com.example.bms.event;

/** 一次监视事件的来源协议，用于接收量统计与处理链追踪。 */
public enum EventSource {
    SYSLOG, SNMP_TRAP, SNMP_GET, TCP_PING, SYSTEM
}

