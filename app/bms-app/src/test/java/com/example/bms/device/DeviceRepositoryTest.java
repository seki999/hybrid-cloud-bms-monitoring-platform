package com.example.bms.device;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
/**
 * 验证设备仓储派生查询与实体映射能够在测试数据库上正确执行。
 * 这是持久化切片测试，专门捕获方法命名、SQL 方言和约束映射问题。
 */
class DeviceRepositoryTest {
    @Autowired DeviceRepository repository;

    @Test void flywaySeedAndSearchAreAvailable() {
        assertEquals(10, repository.count());
        assertEquals(3, repository.findByNameContainingIgnoreCaseOrHostnameContainingIgnoreCase(
                "ルーター", "ルーター", PageRequest.of(0, 20)).getTotalElements());
    }
}
