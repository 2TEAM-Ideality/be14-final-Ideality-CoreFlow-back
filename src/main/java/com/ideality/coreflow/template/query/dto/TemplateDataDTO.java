package com.ideality.coreflow.template.query.dto;

import java.util.List;

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
public class TemplateDataDTO {
	private List<TemplateNodeDTO> nodeList;
	private List<EdgeDTO> edgeList;
}
