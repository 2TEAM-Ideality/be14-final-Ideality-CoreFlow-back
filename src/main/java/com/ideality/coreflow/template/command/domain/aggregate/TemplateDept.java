package com.ideality.coreflow.template.command.domain.aggregate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@Table(name="template_dept")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TemplateDept {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name="template_id", nullable = false)
	private Long templateId;

	@Column(name="dept_id", nullable = false)
	private Long deptId;

}
