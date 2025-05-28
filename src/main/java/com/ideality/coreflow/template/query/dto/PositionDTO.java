package com.ideality.coreflow.template.query.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PositionDTO {
	private int x;
	private int y;
}

// todo. 태스크 생성에서 공통으로 사용하면 될 듯 -> 폴더 구조 얘기할 것