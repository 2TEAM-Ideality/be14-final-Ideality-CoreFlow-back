package com.ideality.coreflow.comment.command.application.dto;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class RequestCommentDTO {
    private String content;
    private Boolean isNotice;
    private Long userId;
    private Long parentCommentId;
    private MultipartFile attachmentFile;
}
