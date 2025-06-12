package com.ideality.coreflow.comment.query.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class ResponseCommentForModifyDTO {
    private Long commentId;
    private String content;
    private Boolean isNotice;
    private Long userId;
}
