package com.ideality.coreflow.template.command.application.dto;

import java.util.List;
import java.util.Map;

import com.ideality.coreflow.template.query.dto.EdgeDTO;
import com.ideality.coreflow.template.query.dto.TemplateInfoDTO;
import com.ideality.coreflow.template.query.dto.TemplateNodeDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RequestUpdateTemplateDTO {

	private TemplateInfoDTO templateInfo;

	private List<TemplateNodeDTO> nodeList;
	private List<EdgeDTO> edgeList;

}
