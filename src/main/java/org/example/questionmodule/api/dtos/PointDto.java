package org.example.questionmodule.api.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PointDto {
    public String article;
    public String clause;
    public String code;
    public String content;
}
