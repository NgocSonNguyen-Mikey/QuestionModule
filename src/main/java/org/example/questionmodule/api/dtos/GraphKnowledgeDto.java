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
public class GraphKnowledgeDto {
    String clause;
    String article;
    String point;
    List<LawDto> laws;
}
