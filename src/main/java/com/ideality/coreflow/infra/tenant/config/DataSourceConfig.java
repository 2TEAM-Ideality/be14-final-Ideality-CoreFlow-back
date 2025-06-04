package com.ideality.coreflow.infra.tenant.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

import javax.sql.DataSource;
import java.util.HashMap;

@Configuration
@RequiredArgsConstructor
public class DataSourceConfig {

    private final JpaProperties jpaProperties;
    private final Environment env;

    @Bean
    @Primary
    public DataSource DataSource(TenantDataSourceProvider tenantDataSourceProvider) {
        TenantRoutingDataSource routingDataSource = new TenantRoutingDataSource(tenantDataSourceProvider);

        routingDataSource.setDefaultTargetDataSource(masterDataSource());

        routingDataSource.setTargetDataSources(new HashMap<>()); // 빈으로 등록 후 런타임에 동적 구성 가능

        return routingDataSource;
    }

    @Bean
    public DataSource masterDataSource() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(env.getProperty("spring.datasource.master.url"));
        config.setUsername(env.getProperty("spring.datasource.master.username"));
        config.setPassword(env.getProperty("spring.datasource.master.password"));
        config.setDriverClassName(env.getProperty("spring.datasource.master.driver-class-name"));
        config.setPoolName(env.getProperty("spring.datasource.master.poolName"));

        return new HikariDataSource(config);
    }

    @Bean
    public JdbcTemplate masterJdbcTemplate() {
        return new JdbcTemplate(masterDataSource());
    }

    @Bean
    public JdbcTemplate tenantJdbcTemplate(DataSource routingDataSource) {
        return new JdbcTemplate(routingDataSource);
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource routingDataSource) {
        var vendorAdapter = new HibernateJpaVendorAdapter();
        var factory = new LocalContainerEntityManagerFactoryBean();
        factory.setDataSource(routingDataSource);
        factory.setPackagesToScan(
            "com.ideality.coreflow.tenant.command.domain.aggregate",
            "com.ideality.coreflow.user.command.domain.aggregate",
            "com.ideality.coreflow.template.command.domain.aggregate",
            "com.ideality.coreflow.attachment.command.domain.aggregate",
            "com.ideality.coreflow.project.command.domain.aggregate",
            "com.ideality.coreflow.holiday.command.domain.aggregate",
            "com.ideality.coreflow.org.command.domain.aggregate"
        );
        factory.setJpaVendorAdapter(vendorAdapter);
        factory.setJpaPropertyMap(jpaProperties.getProperties());
        return factory;
    }
}
