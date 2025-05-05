package org.example.questionmodule.api.services.mapper;

import org.example.questionmodule.api.dtos.GraphKnowledgeDto;
import org.example.questionmodule.api.entities.GraphKnowledge;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {GraphKnowledgeMapper.class})
public interface GraphKnowledgeMapper {

    @Mapping(source = "clause.content", target = "clause")
    @Mapping(source = "article.content", target = "article")
    @Mapping(source = "point.content", target = "point")
    GraphKnowledgeDto toDto(GraphKnowledge graphKnowledge);
}
