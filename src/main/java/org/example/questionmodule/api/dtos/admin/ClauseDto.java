package org.example.questionmodule.api.dtos.admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.questionmodule.api.dtos.TripleDto;
import org.example.questionmodule.api.entities.Article;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClauseDto {
    private String id;

    private String code;

    private String content;

    private List<PointDto> points;

    private String replaceBy;

    List<TripletDto> graph;
}
