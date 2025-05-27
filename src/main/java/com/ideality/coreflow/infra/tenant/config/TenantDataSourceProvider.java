package com.ideality.coreflow.infra.tenant.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@RequiredArgsConstructor
public class TenantDataSourceProvider {
    private final JdbcTemplate masterJdbcTemplate;
    private final TenantDataSourceProperties tenantProps;

    private final Map<Object, Object> dataSourceMap = new ConcurrentHashMap<>();

    public DataSource getDataSource(String tenant) {
        // 이미 생성된 DataSource가 있다면 중복 생성 방지
        if(dataSourceMap.containsKey(tenant)) {
            return (DataSource) dataSourceMap.get(tenant);
        }

        log.info("Creating new DataSource for tenant: {}", tenant);

        String schemaName = "";
        // 마스터 db에서 스키마 정보 조회

        // --------------------------

        try {
            // HikariConfig 설정
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(tenantProps.getUrl() + schemaName);
            config.setUsername(tenantProps.getUsername());
            config.setPassword(tenantProps.getPassword());
            config.setDriverClassName(tenantProps.getDriverClassName());
            config.setMinimumIdle(tenantProps.getMinimumIdle());
            config.setMaximumPoolSize(tenantProps.getMaximumPoolSize());
            config.setIdleTimeout(tenantProps.getIdleTimeout());
            config.setConnectionTimeout(tenantProps.getConnectionTimeout());
            config.setPoolName(tenantProps.getPoolName() + "-" + schemaName);

            HikariDataSource dataSource = new HikariDataSource(config);
            dataSourceMap.put(tenant, dataSource);
            return dataSource;
        } catch (EmptyResultDataAccessException e) {
            throw new RuntimeException("Tenant DB 정보가 없습니다: " + tenant);
        }
    }
}
