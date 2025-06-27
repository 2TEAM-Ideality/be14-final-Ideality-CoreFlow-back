package com.ideality.coreflow.attachment.query.dto;

import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class ResponseCommentAttachmentDTO {
    private Long attachmentId;
    private String originName;
    private String fileType;
    private LocalDateTime uploadAt;
    private Long userId;
    private String userName;
    private String deptName;
}
