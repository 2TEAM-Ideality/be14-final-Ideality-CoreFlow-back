package com.ideality.coreflow.project.command.domain.aggregate;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "participant")
@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Setter
public class Participant {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "target_type")
	@Enumerated(EnumType.STRING)
	private TargetType targetType;

	@Column(name = "target_id")
	private Long targetId;

	@Column(name = "user_id")
	private Long userId;

	@Column(name = "role_id")
	private Long roleId;

	public void changeRoleId() {
		roleId = 2L;
	}
}