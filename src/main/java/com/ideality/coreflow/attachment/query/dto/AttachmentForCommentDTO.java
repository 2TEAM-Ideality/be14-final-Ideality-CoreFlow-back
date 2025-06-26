package com.ideality.coreflow.attachment.query.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class AttachmentForCommentDTO {
    private Long attachmentId;
    private Long commentId;
    private String originName;
}
