package org.example.questionmodule.api.services.interfaces;

import org.example.questionmodule.api.dtos.admin.ConceptDto;
import org.example.questionmodule.api.dtos.admin.RelationDto;
import org.springframework.data.domain.Page;

public interface OntologyService {
    Page<ConceptDto> getAllConcept(Integer page, Integer size);
    Page<RelationDto> getAllRelation(Integer page, Integer size);
    ConceptDto updateConcept(String id, ConceptDto dto);
    RelationDto updateRelation(String id, RelationDto dto);
    RelationDto createRelation(RelationDto dto);
    ConceptDto createConcept(ConceptDto dto);
}
