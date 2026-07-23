package com.example.bms.protocol.snmp;

/** SNMP GET 输入；community 从 Secret 注入，不得写入日志或结果。 */
public record SnmpGetRequest(String host, int port, String oid, String community,
                             int timeoutMillis, int retries) { }

