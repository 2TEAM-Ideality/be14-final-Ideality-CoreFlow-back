package com.ideality.coreflow.attachment.command.application.dto;
import com.ideality.coreflow.infra.s3.UploadFileResult;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class CreateAttachmentDTO {
    private String originalName;
    private String storedName;
    private String url;
    private String fileType;
    private String size;

    public CreateAttachmentDTO(UploadFileResult uploadResult) {
        this.originalName = uploadResult.getOriginalName();
        this.storedName = uploadResult.getStoredName();
        this.url = uploadResult.getUrl();
        this.fileType = uploadResult.getFileType();
        this.size = uploadResult.getSize();
    }
}
