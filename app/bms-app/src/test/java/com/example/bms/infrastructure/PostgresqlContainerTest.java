package com.example.bms.infrastructure;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.sql.DriverManager;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/** Docker 可用时验证真实 PostgreSQL JDBC；不可用时由 Testcontainers 明确跳过。 */
@Testcontainers(disabledWithoutDocker = true)
class PostgresqlContainerTest {
    @Container static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:16.8-alpine");

    @Test void connectsToRealPostgresql() throws Exception {
        try (var connection = DriverManager.getConnection(POSTGRES.getJdbcUrl(), POSTGRES.getUsername(), POSTGRES.getPassword());
             var statement = connection.createStatement();
             var result = statement.executeQuery("SELECT 1")) {
            result.next();
            assertEquals(1, result.getInt(1));
        }
    }
}

