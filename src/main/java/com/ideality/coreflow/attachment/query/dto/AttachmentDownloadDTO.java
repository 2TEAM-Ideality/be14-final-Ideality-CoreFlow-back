package com.ideality.coreflow.attachment.query.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class AttachmentDownloadDTO {
    private String url;
    private String originName;
}
