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

	@ManyToOne
	@JoinColumn(name = "work_id")
	private Work work;

	@ManyToOne
	@JoinColumn(name = "dept_id")
	private Dept dept;

	@Column(name = "is_deleted")
	@Builder.Default
	private boolean isDeleted = false;
}