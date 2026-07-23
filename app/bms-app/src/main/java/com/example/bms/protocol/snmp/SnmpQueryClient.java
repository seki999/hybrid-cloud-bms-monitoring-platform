package com.example.bms.protocol.snmp;

/**
 * SNMP 主动查询扩展点。
 *
 * <p>当前实现为 v2c；生产加入 v3 时实现本接口并使用 USM 用户/认证/隐私参数，无需改变告警引擎。</p>
 */
public interface SnmpQueryClient {
    SnmpGetResult get(SnmpGetRequest request);
}

