package com.ideality.coreflow.comment.query.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class ResponseCommentsDTO {
    private Long commentId;
    private Long parentCommentId;
    private Long userId;
    private String commentWriter;
    private String content;
}
