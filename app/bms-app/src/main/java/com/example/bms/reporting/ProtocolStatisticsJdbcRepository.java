package com.example.bms.reporting;

import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.stereotype.Repository;

/**
 * 使用 JdbcTemplate 执行跨实体聚合报表。
 *
 * <p>简单 CRUD 继续交给 JPA；这种只返回投影的 GROUP BY 查询不需要构造实体图，直接 JDBC 更清楚。</p>
 */
@Repository
public class ProtocolStatisticsJdbcRepository {
    private final JdbcTemplate jdbc;

    public ProtocolStatisticsJdbcRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    /** 返回各协议来源的事件总数；LinkedHashMap 保持数据库排序，方便稳定渲染与测试。 */
    public Map<String, Long> countEventsBySource() {
        Map<String, Long> result = new LinkedHashMap<>();
        jdbc.query("select source, count(*) as event_count from monitoring_events group by source order by source",
                (RowCallbackHandler) row ->
                        result.put(row.getString("source"), row.getLong("event_count")));
        return result;
    }
}
