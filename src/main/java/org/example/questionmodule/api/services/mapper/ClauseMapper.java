package org.example.questionmodule.api.services.mapper;


import org.example.questionmodule.api.dtos.ClauseDto;
import org.example.questionmodule.api.entities.Clause;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = {PointMapper.class})
public interface ClauseMapper {
    @Mapping(source = "article.code", target = "article")
    ClauseDto toDto(Clause clause);

    org.example.questionmodule.api.dtos.admin.ClauseDto toAdminDto(Clause clause);

    List<ClauseDto> toDtoList(List<Clause> clauses);

    List<org.example.questionmodule.api.dtos.admin.ClauseDto> toAdminDtoList(List<Clause> clauses);
}
