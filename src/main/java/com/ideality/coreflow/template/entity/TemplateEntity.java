package com.ideality.coreflow.template.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Entity
@Table(name="template")
@Builder
@RequiredArgsConstructor
@Getter
public class TemplateEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="id")
	private Long id;		// 템플릿 아이디
	
	private String name; 	// 템플릿 이름

	private String description; 	// 템플릿 설명

	private LocalDateTime createdAt;	// 생성일
	private LocalDateTime updatedAt;	// 수정일

	private int duration; 			// 소요일
	
	private int tastCount;			// 전체 태스크 개수

	// TODO. 회원이랑 엮어야 함.
	private int createdBy;			// 생성자
	private int updatedBy;			// 수정자

}
