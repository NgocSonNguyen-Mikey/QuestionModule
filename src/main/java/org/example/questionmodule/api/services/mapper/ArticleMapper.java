package org.example.questionmodule.api.services.mapper;

import org.example.questionmodule.api.dtos.ArticleDto;
import org.example.questionmodule.api.entities.Article;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring", uses = {ClauseMapper.class})
public interface ArticleMapper {
    ArticleDto toDto(Article article);

    org.example.questionmodule.api.dtos.admin.ArticleDto toAdminDto(Article article);

    List<org.example.questionmodule.api.dtos.admin.ArticleDto> toAdminDtoList(List<Article> articles);
}
