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

// dataSource 생성만
@Slf4j
@Component
@RequiredArgsConstructor
public class TenantDataSourceProvider {
    private final TenantDataSourceProperties tenantProps;
    private final Map<Object, Object> dataSourceMap = new ConcurrentHashMap<>();

    public DataSource createDataSource(String schemaName) {

        if(dataSourceMap.containsKey(schemaName)) {
            log.info("DataSource {} already exists", schemaName);
            return (DataSource) dataSourceMap.get(schemaName);
        }

        log.info("Creating new DataSource for schemaName: {}", schemaName);

        // HikariConfig 설정
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(tenantProps.getUrl() + schemaName);
        log.info("테넌트 url주소: {}", config.getJdbcUrl());
        config.setUsername(tenantProps.getUsername());
        config.setPassword(tenantProps.getPassword());
        config.setDriverClassName(tenantProps.getDriverClassName());
        config.setMinimumIdle(tenantProps.getMinimumIdle());
        config.setMaximumPoolSize(tenantProps.getMaximumPoolSize());
        config.setIdleTimeout(tenantProps.getIdleTimeout());
        config.setConnectionTimeout(tenantProps.getConnectionTimeout());
        config.setPoolName(tenantProps.getPoolName() + "-" + schemaName);

        return new HikariDataSource(config);
    }
}
