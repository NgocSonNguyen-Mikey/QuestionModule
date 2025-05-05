package org.example.questionmodule.api.dtos.admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.questionmodule.api.dtos.admin.TripletDto;
import org.example.questionmodule.api.entities.Clause;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PointDto {
    private String id;

    private String code;

    private String content;

    private Clause clause;

    private String replaceBy;

    List<TripletDto> graph;
}
