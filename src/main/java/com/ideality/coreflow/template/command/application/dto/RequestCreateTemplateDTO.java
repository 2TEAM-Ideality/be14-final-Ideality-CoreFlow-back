package com.ideality.coreflow.template.command.application.dto;

import java.util.List;

import com.ideality.coreflow.template.query.dto.EdgeDTO;
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
public class RequestCreateTemplateDTO {
	String name;
	String description;
	Long createdBy;
	int duration;
	int taskCount;

	List<TemplateNodeDTO> nodeList;
	List<EdgeDTO> edgeList;

}
