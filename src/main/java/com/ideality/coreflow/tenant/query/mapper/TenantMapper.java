package com.ideality.coreflow.tenant.query.mapper;

import com.ideality.coreflow.tenant.query.dto.ResponseSchemaInfo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface TenantMapper{
    List<ResponseSchemaInfo> selectAllTenant();
}
