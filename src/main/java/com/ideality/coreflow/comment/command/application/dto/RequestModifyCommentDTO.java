package com.ideality.coreflow.comment.command.application.dto;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class RequestModifyCommentDTO {
    private String content;
    private Boolean isNotice;
    private MultipartFile attachmentFile;
}
