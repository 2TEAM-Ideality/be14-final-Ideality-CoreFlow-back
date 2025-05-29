package com.ideality.coreflow.infra.tenant.config;

import com.ideality.coreflow.common.exception.BaseException;
import com.ideality.coreflow.common.exception.ErrorCode;
import jakarta.annotation.Nullable;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.lang.NonNull;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TenantRoutingDataSource extends AbstractRoutingDataSource {

    private final Map<Object, Object> dataSources = new ConcurrentHashMap<>();
    private final TenantDataSourceProvider tenantDataSourceProvider;

    public TenantRoutingDataSource(TenantDataSourceProvider tenantDataSourceProvider) {
        this.tenantDataSourceProvider = tenantDataSourceProvider;
        super.setTargetDataSources(dataSources);
        super.afterPropertiesSet();
    }

    @Override
    protected Object determineCurrentLookupKey() {
        return TenantContext.getTenant();   // 현재 스레드의 테넌트 스키마 명
    }

    @Override
    protected DataSource determineTargetDataSource() {
        String schema = (String) determineCurrentLookupKey();

        if (schema == null) {
            // defaultDataSource가 있으면 그것을 쓰도록 위임
            return super.determineTargetDataSource();
        }

        DataSource dataSource = (DataSource) dataSources.get(schema);
        if (dataSource != null) {
            return dataSource;
        }

        synchronized (dataSources) {
            dataSource = (DataSource) dataSources.get(schema);
            if (dataSource == null) {
                dataSource = tenantDataSourceProvider.createDataSource(schema);
                dataSources.put(schema, dataSource);

                // AbstractRoutingDataSource가 캐시하는 내부맵을 새로 세팅해야함
                super.setTargetDataSources(new HashMap<>(dataSources));
                super.afterPropertiesSet();
            }
            return dataSource;
        }
    }
}