package com.ideality.coreflow.project.command.domain.aggregate;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "work_dept")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkDept {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "work_id", nullable = false)
	private Long workId;

	@Column(name = "dept_id", nullable = false)
	private Long deptId;

	@Column(name = "is_deleted")
	@Builder.Default
	private boolean isDeleted = false;
}