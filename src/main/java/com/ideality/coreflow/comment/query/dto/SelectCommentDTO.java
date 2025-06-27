package com.ideality.coreflow.comment.query.dto;

import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class SelectCommentDTO {
    private Long commentId;
    private Long parentCommentId;
    private Long userId;
    private String profileImage;
    private String name;
    private String deptName;
    private String content;
    private LocalDateTime createdAt;
    private Boolean isModify;

    private Long attachmentId;
    private String originName;
}
