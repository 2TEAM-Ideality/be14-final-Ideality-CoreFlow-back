package com.ideality.coreflow.project.command.domain.aggregate;


import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDate;
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
@Table(name = "work")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class Work {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String name;

	private String description;

	@Column(name = "created_at", nullable = false)
	private LocalDateTime createdAt;

	@Column(name = "start_base", nullable = false)
	private LocalDate startBase;

	@Column(name = "end_base", nullable = false)
	private LocalDate endBase;

	@Column(name = "start_expect", nullable = false)
	private LocalDate startExpect;

	@Column(name = "end_expect", nullable = false)
	private LocalDate endExpect;

	@Column(name = "start_real")
	private LocalDate startReal;

	@Column(name = "end_real")
	private LocalDate endReal;

	@Column(name = "progress_rate", nullable = false)
	@ColumnDefault("0")
	private Double progressRate;

	@Column(name = "passed_rate", nullable = false)
	@ColumnDefault("0")
	private Double passedRate;

	private Status status;

	@Column(name = "slack_time", nullable = false)
	@ColumnDefault("0")
	private int slackTime;

	@Column(name = "parent_task_id")
	private Long parentTaskId;

	@Column(name = "project_id")
	private Long projectId;
}

