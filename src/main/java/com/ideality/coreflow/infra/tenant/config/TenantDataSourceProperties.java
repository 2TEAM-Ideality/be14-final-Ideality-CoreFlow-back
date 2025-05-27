package com.ideality.coreflow.infra.tenant.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "spring.datasource.tenant")
public class TenantDataSourceProperties {
    private String url;
    private String driverClassName;
    private String username;
    private String password;
    private int minimumIdle;
    private int maximumPoolSize;
    private long idleTimeout;
    private long connectionTimeout;
    private String poolName;
}
