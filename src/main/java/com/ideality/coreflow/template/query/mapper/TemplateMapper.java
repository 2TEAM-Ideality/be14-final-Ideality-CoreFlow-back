package com.ideality.coreflow.template.query.mapper;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.ideality.coreflow.template.query.dto.TemplateDetailDBDTO;
import com.ideality.coreflow.template.query.dto.TemplateListResponseDTO;

@Mapper
public interface TemplateMapper {

	List<TemplateListResponseDTO> selectAllTemplates();

	TemplateDetailDBDTO selectTemplateDetail(Integer templateId);
}
