package com.ideality.coreflow.comment.command.application.dto;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class RequestCommentDTO {
    private String content;
    private Boolean isNotice;
    private Long parentCommentId;
    private List<String> mentions;
    private List<String> details;
    private MultipartFile attachmentFile;
}
