package com.ideality.coreflow.tenant.command.domain.aggregate;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "erp_master")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErpMaster {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "company_code")
    private String companyCode;

    @Column(name = "company_name")
    private String companyName;

    @Column(name = "company_schema")
    private String companySchema;
}