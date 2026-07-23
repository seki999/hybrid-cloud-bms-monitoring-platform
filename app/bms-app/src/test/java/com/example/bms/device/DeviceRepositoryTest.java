package com.example.bms.device;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
class DeviceRepositoryTest {
    @Autowired DeviceRepository repository;

    @Test void flywaySeedAndSearchAreAvailable() {
        assertEquals(10, repository.count());
        assertEquals(3, repository.findByNameContainingIgnoreCaseOrHostnameContainingIgnoreCase(
                "ルーター", "ルーター", PageRequest.of(0, 20)).getTotalElements());
    }
}

