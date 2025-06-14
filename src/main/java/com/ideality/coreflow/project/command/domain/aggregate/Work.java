package com.ideality.coreflow.project.command.domain.aggregate;


import com.ideality.coreflow.common.exception.BaseException;
import com.ideality.coreflow.common.exception.ErrorCode;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.*;


@Entity
@Table(name = "work")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
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
	@Builder.Default
	private Double progressRate = 0.0;

	@Column(name = "passed_rate", nullable = false)
	@Builder.Default
	private Double passedRate = 0.0;

	@Column(name = "delay_days", nullable = false)
	private Integer delayDays = 0;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private Status status;

	@Column(name = "slack_time", nullable = false)
	@Builder.Default
	private int slackTime = 0;

	@Column(name = "parent_task_id")
	private Long parentTaskId;

	@Column(name = "project_id")
	private Long projectId;

	public void startTask() {
		if (this.status == Status.PROGRESS) {
			throw new BaseException(ErrorCode.INVALID_STATUS_PROGRESS);
		}

		this.status = Status.PROGRESS;
		this.startReal = LocalDate.now();
	}

	public void endTask() {
		if (this.status == Status.COMPLETED || this.status == Status.PENDING) {
			throw new BaseException(ErrorCode.INVALID_STATUS_COMPLETED);
		}

		this.status = Status.COMPLETED;
		this.endReal = LocalDate.now();
	}

	public void softDeleteTask() {
		if (this.status == Status.DELETED) {
			throw new BaseException(ErrorCode.INVALID_STATUS_DELETED);
		}
		this.status = Status.DELETED;
	}

}

