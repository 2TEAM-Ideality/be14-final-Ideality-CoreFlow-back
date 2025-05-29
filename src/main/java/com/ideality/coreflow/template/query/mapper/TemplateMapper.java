package com.ideality.coreflow.template.query.mapper;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.ideality.coreflow.template.query.dto.RequestTemplateDetailDTO;
import com.ideality.coreflow.template.query.dto.ResponseTemplateListDTO;

@Mapper
public interface TemplateMapper {

	List<ResponseTemplateListDTO> selectAllTemplates();

	RequestTemplateDetailDTO selectTemplateDetail(Long templateId);
}
