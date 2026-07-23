package com.example.bms.security;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/** 使用常量时间比较验证外部采集 API 密钥，降低长度/前缀时序泄漏。 */
@Service
public class ApiKeyService {
    private final byte[] expected;

    public ApiKeyService(@Value("${bms.ingest.api-key}") String expected) {
        this.expected = expected.getBytes(StandardCharsets.UTF_8);
    }

    /** @return 提供值与外部化配置完全一致时为 true */
    public boolean isValid(String provided) {
        return provided != null && MessageDigest.isEqual(expected, provided.getBytes(StandardCharsets.UTF_8));
    }
}

