package com.ideality.coreflow.project.command.domain.aggregate;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "work_dept")
@Getter
@Setter
@NoArgsConstructor
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
	private boolean isDeleted = false;
}