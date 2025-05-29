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
    private final TenantDataSourceProvider tenantDataSourceProvider;


    @Bean
    @Primary
    public DataSource DataSource() {
        TenantRoutingDataSource routingDataSource = new TenantRoutingDataSource(tenantDataSourceProvider);
        routingDataSource.setDefaultTargetDataSource(masterDataSource());
        routingDataSource.setTargetDataSources(new HashMap<>()); // 빈으로 등록 후 런타임에 동적 구성 가능
        routingDataSource.afterPropertiesSet();
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
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource) {
        var vendorAdapter = new HibernateJpaVendorAdapter();
        var factory = new LocalContainerEntityManagerFactoryBean();
        factory.setDataSource(dataSource);
        factory.setPackagesToScan(
                "com.ideality.coreflow.common.tenant",
                "com.ideality.coreflow.tenant.domain.aggregate",
                "com.ideality.coreflow.user.command.domain.aggregate"
        );
        factory.setJpaVendorAdapter(vendorAdapter);
        factory.setJpaPropertyMap(jpaProperties.getProperties());
        return factory;
    }
}
