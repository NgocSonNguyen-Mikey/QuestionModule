package org.example.questionmodule.api.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClauseDto {
    public String article;
    public String code;
    public String content;
    public List<PointDto> points;
}
