package com.ideality.coreflow.project.query.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RelationDTO {
	private Long relationId;
	private Long nextWorkId;
	private Long prevWorkId;
}
