package com.ideality.coreflow.attachment.command.application.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AttachmentPreviewDTO {
    String originName;
    String url;
}
