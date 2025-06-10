package com.ideality.coreflow.infra.tenant.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
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
        log.info("determine current tenant: " + TenantContext.getTenant());
        return TenantContext.getTenant();   // 현재 스레드의 테넌트 스키마 명
    }

    @Override
    protected DataSource determineTargetDataSource() {
        log.info("determineTargetDataSource");
        String schema = (String) determineCurrentLookupKey();

        if (schema == null) {
            // defaultDataSource가 있으면 그것을 쓰도록 위임
            log.info("schema is null");
            return super.determineTargetDataSource();
        }

        DataSource dataSource = (DataSource) dataSources.get(schema);
        if (dataSource != null) {
            log.info("determine target data source: " + dataSource);
            return dataSource;
        }

        synchronized (dataSources) {
            dataSource = (DataSource) dataSources.get(schema);
            if (dataSource == null) {
                log.info("데이터 소스 맵에 해당 데이터소스가 없다. 생성 시작");
                dataSource = tenantDataSourceProvider.createDataSource(schema);
                log.info("생성하고 왔어욤");
                dataSources.put(schema, dataSource);

                // AbstractRoutingDataSource가 캐시하는 내부맵을 새로 세팅해야함
                super.setTargetDataSources(new HashMap<>(dataSources));
                super.afterPropertiesSet();
            }
            return dataSource;
        }
    }

    public DataSource getCurrentDataSource() {
        // 내부 메서드인 determineTargetDataSource() 호출해서 반환
        return (DataSource) determineTargetDataSource();
    }
}