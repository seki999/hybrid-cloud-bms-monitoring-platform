package com.example.bms.protocol.snmp;

/** SNMP GET 结构化结果。 */
public record SnmpGetResult(boolean success, String oid, String value, long elapsedMillis,
                            String errorType, String errorMessage, int retries) { }

