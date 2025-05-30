package com.ideality.coreflow.template.command.domain.aggregate;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name="template")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Template {

	@Getter
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="id")
	private Long id;		// 템플릿 아이디

	@Column(name="name")
	private String name; 	// 템플릿 이름

	@Column(name="description")
	private String description; 	// 템플릿 설명

	@Column(name="created_at", nullable=false, updatable=false)
	private LocalDateTime createdAt;	// 생성일

	@Column(name="updated_at")
	private LocalDateTime updatedAt;	// 수정일

	// 설명. 다른 도메인에 위치한 Entity와는 ManytoMany 같은 관계 연결 X
	// 유저 id 기반으로 UserService 로부터 회원 정보 가져오는 방식으로 사용하기
	@Column(name="created_by", nullable=false, updatable=false)
	private Long createdBy;			// 생성자

	@Column(name="updated_by")
	private Long updatedBy;			// 수정자

	@Column(name="duration", nullable=false)
	private int duration; 			// 소요일

	@Column(name="task_count", nullable=false)
	private int taskCount;			// 전체 태스크 개수

	@Column(name="is_deleted", nullable=false )
	private boolean isDeleted = false; 		// 삭제 여부

}

