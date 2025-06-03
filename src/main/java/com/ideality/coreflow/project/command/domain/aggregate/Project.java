package com.ideality.coreflow.project.command.domain.aggregate;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.ideality.coreflow.template.command.domain.aggregate.Template;

@Entity
@Table(name = "project")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Project {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String name;

	@Column(nullable = false, columnDefinition = "TEXT")
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
	private Double progressRate = 0.0;

	@Column(name = "passed_rate", nullable = false)
	private Double passedRate = 0.0;

	@Column(name = "delay_days", nullable = false)
	private Integer delayDays = 0;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private Status status = Status.PENDING;

//	@ManyToOne(fetch = FetchType.LAZY)
//	@JoinColumn(name = "template_id")
//	private Template template;
	@Column(name = "template_id")
	private Long templateId;

}

