package com.ideality.coreflow.infra.s3;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class UploadFileResult {
    private String originalName;
    private String storedName;
    private String url;
    private String fileType;
    private String size;
}
