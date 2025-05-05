package org.example.questionmodule.api.services.mapper;

import org.example.questionmodule.api.dtos.admin.ConceptDto;
import org.example.questionmodule.api.entities.Concept;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {ConceptMapper.class})
public interface ConceptMapper {
    ConceptDto toDto(Concept concept);
}
