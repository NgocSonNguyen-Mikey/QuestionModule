package org.example.questionmodule.api.services.interfaces;

import org.example.questionmodule.api.dtos.admin.ConceptDto;
import org.example.questionmodule.api.dtos.admin.RelationDto;
import org.example.questionmodule.utils.dtos.ListResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface OntologyService {
    ListResponse<ConceptDto> getAllConcept();
    ListResponse<RelationDto> getAllRelation();
    ConceptDto updateConcept(String id, ConceptDto dto);
    RelationDto updateRelation(String id, RelationDto dto);
    RelationDto createRelation(RelationDto dto);
    ConceptDto createConcept(ConceptDto dto);
}
