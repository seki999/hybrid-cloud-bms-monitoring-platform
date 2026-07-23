package com.example.bms.common.exception;

/** 输入格式正确但违反业务状态机时使用，例如重复关闭告警。 */
public class BusinessException extends RuntimeException {
    public BusinessException(String message) { super(message); }
}

