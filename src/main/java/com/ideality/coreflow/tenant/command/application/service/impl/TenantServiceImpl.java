package com.ideality.coreflow.tenant.command.application.service.impl;

import com.ideality.coreflow.common.exception.BaseException;
import com.ideality.coreflow.common.exception.ErrorCode;
import com.ideality.coreflow.infra.tenant.config.TenantContext;
import com.ideality.coreflow.tenant.command.application.dto.RequestCreateTenant;
import com.ideality.coreflow.tenant.command.application.service.TenantService;
import com.ideality.coreflow.tenant.command.domain.aggregate.ErpMaster;
import com.ideality.coreflow.tenant.command.domain.repository.TenantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

@Service
@RequiredArgsConstructor
@Slf4j
public class TenantServiceImpl implements TenantService {

    private final TenantRepository tenantRepository;
    private final DataSource dataSource;
    private final Environment env;

    @Override
    public String findSchemaNameByCompanyCode(String companyCode) {

        ErpMaster tenantInfo = tenantRepository.findByCompanyCode(companyCode)
                .orElseThrow(() -> new BaseException(ErrorCode.NOT_FOUND));

        log.info("tenantInfo={}", tenantInfo);

        return tenantInfo.getCompanySchema();
    }

    @Override
    public void createNewTenant(String tenantName) {
        validateName(tenantName);
        log.info("생성해볼까");

        try (Connection connection = dataSource.getConnection();
             Statement stmt = connection.createStatement()) {

            // DB 생성
            log.info("stmt.execute");
            stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS " + tenantName);

            new Thread(() -> {
                //DB 접속후 테이블 초기화
                log.info("새 스레드");
                TenantContext.setTenant(tenantName);
                try (Connection newDbConnection = DriverManager.getConnection(
                        env.getProperty("spring.datasource.tenant.url") + tenantName, env.getProperty("spring.datasource.tenant.username"), env.getProperty("spring.datasource.tenant.password")
                )) {
                    try (Statement initStmt = newDbConnection.createStatement()) {
                        String ddl = loadInitScript(); // 테이블 생성 SQL
                        for (String sql : ddl.split(";")) {
                            if (!sql.trim().isEmpty()) {
                                initStmt.execute(sql);
                            }
                        }
                    }
                } catch (SQLException e) {
                    throw new BaseException(ErrorCode.TENANT_CREATE_FAILED);
                }

            }).start();

        } catch (SQLException e) {
            throw new BaseException(ErrorCode.TENANT_CREATE_FAILED);
        }
    }

    @Override
    public Long registTenant(RequestCreateTenant request) {

        if (tenantRepository.existsByCompanyCode(request.getCompanyCode())) {
            throw new BaseException(ErrorCode.DUPLICATED_COMPANY_CODE);
        }

        ErpMaster tenantInfo = ErpMaster.builder()
                .companyCode(request.getCompanyCode())
                .companyName(request.getCompanyName())
                .companySchema(request.getSchemaName())
                .build();

        return tenantRepository.save(tenantInfo).getId();
    }

    private void validateName(String name) {
        if (!name.matches("[a-zA-Z0-9_]+")) {
            throw new BaseException(ErrorCode.ILLEGAL_TENANT_NAME);
        }
    }

    private String loadInitScript() {
        try {
            log.info("DDL");
            Resource resource = new ClassPathResource("sql/DDL.sql");
            return new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("초기화 스크립트 로딩 실패", e);
        }
    }
}
