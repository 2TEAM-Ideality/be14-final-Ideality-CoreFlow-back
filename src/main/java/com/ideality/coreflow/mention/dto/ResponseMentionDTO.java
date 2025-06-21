package com.ideality.coreflow.mention.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class ResponseMentionDTO {
    private Long id;
    private String name;
    private String type;
}
