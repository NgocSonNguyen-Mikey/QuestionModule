package org.example.questionmodule.api.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LawDto {
    public Date year;
    public String name;
    public List<ArticleDto> articles;
    public List<ClauseDto> clauses;
    public List<PointDto> points;
}
