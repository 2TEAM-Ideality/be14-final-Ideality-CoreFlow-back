package com.ideality.coreflow.template.query.mapper;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.ideality.coreflow.template.query.dto.TemplateInfoDTO;

@Mapper
public interface TemplateMapper {

	List<TemplateInfoDTO> selectAllTemplates();

	TemplateInfoDTO selectTemplateDetail(Long templateId);
}
