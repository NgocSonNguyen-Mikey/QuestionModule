package org.example.questionmodule.api.dtos.admin;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.questionmodule.api.dtos.admin.TripletDto;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArticleDto {

    private String id;

    private String code;

    private String title;

    private String content;

    private List<ClauseDto> clauses;

    private String replaceBy;

    List<TripletDto> graph;
}
