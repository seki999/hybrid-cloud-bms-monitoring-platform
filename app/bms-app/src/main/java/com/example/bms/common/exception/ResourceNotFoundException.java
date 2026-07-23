package com.example.bms.common.exception;

/** 请求引用的业务对象不存在时使用，Web 层统一映射为 404。 */
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) { super(message); }
}

