package com.ideality.coreflow.attachment.command.application.dto;

import com.ideality.coreflow.attachment.command.domain.aggregate.FileTargetType;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RegistAttachmentDTO {
    String originName;
    String storedName;
    String url;
    String fileType;
    String size;
    long targetId;
    long uploaderId;
    FileTargetType targetType;
}
