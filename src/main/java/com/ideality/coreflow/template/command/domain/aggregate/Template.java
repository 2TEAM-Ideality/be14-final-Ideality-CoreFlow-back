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

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="id")
	@Getter
	private Long id;		// 템플릿 아이디

	@Column(name="name")
	private String name; 	// 템플릿 이름

	@Column(name="description")
	private String description; 	// 템플릿 설명

	@Column(name="created_by", nullable=false, updatable=false)
	private Long createdBy;			// 생성자

	@Column(name="created_at", nullable=false, updatable=false)
	private LocalDateTime createdAt;	// 생성일

	@Column(name="updated_at")
	private LocalDateTime updatedAt;	// 수정일

	@Column(name="updated_by")
	private Long updatedBy;			// 수정자

	@Column(name="duration", nullable=false)
	private int duration; 			// 소요일

	@Column(name="task_count", nullable=false)
	private int taskCount;			// 전체 태스크 개수

	@Column(name="is_deleted", nullable=false)
	private boolean isDeleted = false; 		// 삭제 여부

	@Column(name="deleted_at")
	private LocalDateTime deletedAt;

	public void updateTemplate(String name, String description, int duration, int taskCount, Long updatedBy) {
		this.name = name;
		this.description = description;
		this.duration = duration;
		this.taskCount = taskCount;
		this.updatedAt = LocalDateTime.now();
		this.updatedBy = updatedBy;
	}

	public void deleteTemplate() {
		this.isDeleted = true;
		this.deletedAt = LocalDateTime.now();
	}


}

