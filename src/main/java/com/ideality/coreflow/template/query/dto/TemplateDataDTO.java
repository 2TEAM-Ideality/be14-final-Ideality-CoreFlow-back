package com.ideality.coreflow.template.query.dto;

import java.util.List;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class TemplateDataDTO {
	private List<NodeDTO> nodeList;
	private List<EdgeDTO> edgeList;
}
