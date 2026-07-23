package com.example.bms.common.domain;

/**
 * Event 的判定结果和 Alert 生命周期状态。
 * NORMAL/RECOVERED 描述恢复判断，ACKNOWLEDGED/CLOSED 描述操作员动作。
 */
public enum AlertStatus {
    NORMAL, WARNING, CRITICAL, RECOVERED, ACKNOWLEDGED, CLOSED
}

