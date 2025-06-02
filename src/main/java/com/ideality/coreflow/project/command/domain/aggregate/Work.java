package com.ideality.coreflow.project.command.domain.aggregate;


<<<<<<< HEAD
import jakarta.persistence.*;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
=======
import com.ideality.coreflow.common.exception.BaseException;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static com.ideality.coreflow.common.exception.ErrorCode.INVALID_STATUS_TRANSITION;
>>>>>>> 05759c806e57ea1235907586ad2fec5b0dab5b08

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
	@Builder.Default
	private Double progressRate = 0.0;

	@Column(name = "passed_rate", nullable = false)
	@Builder.Default
	private Double passedRate = 0.0;

<<<<<<< HEAD
	@Column(name = "status")
	@Enumerated(EnumType.STRING)  // Enum을 String으로 저장하도록 지정
=======
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
>>>>>>> 05759c806e57ea1235907586ad2fec5b0dab5b08
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
			throw new BaseException(INVALID_STATUS_TRANSITION);
		}

		this.status = Status.PROGRESS;
	}
}

