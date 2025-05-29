package com.ideality.coreflow.infra.tenant.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

// dataSource 생성만
@Slf4j
@Component
@RequiredArgsConstructor
public class TenantDataSourceProvider {
    private final TenantDataSourceProperties tenantProps;

    public DataSource createDataSource(String schemaName) {

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
