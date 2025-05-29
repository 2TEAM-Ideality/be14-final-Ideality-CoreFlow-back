package com.ideality.coreflow.project.command.domain.aggregate;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "dept")
@Getter
@Setter
@NoArgsConstructor
public class Dept {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name="name", nullable = false, unique = true)
	private String name;

	@Column(name="dept_code", nullable = false, length = 10)
	private String deptCode;

	@Column(name="is_deleted", nullable = false)
	private Boolean isDeleted = false;

	@ManyToOne
	@JoinColumn(name = "parent_dept_id")
	private Dept parentDept;

}
