package com.ideality.coreflow.tenant.command.domain.repository;

import com.ideality.coreflow.tenant.command.domain.aggregate.ErpMaster;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TenantRepository extends JpaRepository<ErpMaster, Long> {

    Optional<ErpMaster> findByCompanyCode(String companyCode);

    boolean existsByCompanyCode(String companyCode);
}
