package com.ideality.coreflow.comment.command.application.dto;

import com.ideality.coreflow.comment.command.domain.aggregate.CommentType;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class RequestCommentDTO {
    private String content;
    private Boolean isNotice;
    private CommentType type;
    private Long taskId;
    private Long userId;
    private Long parentCommentId;
}
