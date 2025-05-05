package org.example.questionmodule.api.services.mapper;


import org.example.questionmodule.api.dtos.PointDto;
import org.example.questionmodule.api.entities.Point;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = {ArticleMapper.class})
public interface PointMapper {
    @Mapping(source = "clause.article.code", target = "article")
    @Mapping(source = "clause.code", target = "clause")
    PointDto toDto(Point point);

    org.example.questionmodule.api.dtos.admin.PointDto toAdminDto(Point point);

    List<PointDto> toDtoList(List<Point> points);

    List<org.example.questionmodule.api.dtos.admin.PointDto> toAdminDtoList(List<Point> points);
}
