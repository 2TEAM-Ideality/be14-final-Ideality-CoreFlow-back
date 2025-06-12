package com.ideality.coreflow.attachment.query.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResponseAttachmentDTO {

    private Long id;
    private String url;
    private String fileType;
    private String size;
    private String targetType;
    private Long targetId;
}