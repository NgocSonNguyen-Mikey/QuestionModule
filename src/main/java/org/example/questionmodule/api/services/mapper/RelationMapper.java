package org.example.questionmodule.api.services.mapper;

import org.example.questionmodule.api.dtos.admin.ConceptDto;
import org.example.questionmodule.api.dtos.admin.RelationDto;
import org.example.questionmodule.api.entities.Concept;
import org.example.questionmodule.api.entities.Relation;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {RelationMapper.class})
public interface RelationMapper {
    RelationDto toDto(Relation relation);
}
