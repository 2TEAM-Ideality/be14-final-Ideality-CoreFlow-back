package com.ideality.coreflow.attachment.query.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class GetTaskInfoDTO {
    private Long taskId;
    private Long commentId;
    private String taskName;
}
