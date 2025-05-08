package org.example.questionmodule.api.services;

import lombok.RequiredArgsConstructor;
import org.example.questionmodule.api.dtos.admin.ConceptDto;
import org.example.questionmodule.api.dtos.admin.RelationDto;
import org.example.questionmodule.api.entities.Concept;
import org.example.questionmodule.api.entities.GraphKnowledge;
import org.example.questionmodule.api.entities.Relation;
import org.example.questionmodule.api.entities.Triplet;
import org.example.questionmodule.api.repositories.ConceptRepository;
import org.example.questionmodule.api.repositories.RelationRepository;
import org.example.questionmodule.api.repositories.TripletRepository;
import org.example.questionmodule.api.services.interfaces.OntologyService;
import org.example.questionmodule.api.services.mapper.ConceptMapper;
import org.example.questionmodule.api.services.mapper.RelationMapper;
import org.example.questionmodule.utils.exceptions.DataNotFoundException;
import org.example.questionmodule.utils.exceptions.InputInvalidException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DefaultOntologyService implements OntologyService {

    private final ConceptRepository conceptRepository;
    private final RelationRepository relationRepository;
    private final TripletRepository tripletRepository;
    private final ConceptMapper conceptMapper;
    private final RelationMapper relationMapper;

    @Override
    public Page<ConceptDto> getAllConcept(Integer page, Integer size){
        Pageable pageable = PageRequest.of(page, size);
        Page<Concept> concepts = conceptRepository.findAll(pageable);

        return concepts.map(conceptMapper::toDto);
    }

    @Override
    public Page<RelationDto> getAllRelation(Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Relation> relations = relationRepository.findAll(pageable);

        return relations.map(relationMapper::toDto);
    }

    @Override
    public ConceptDto updateConcept(String id, ConceptDto dto) {
        Concept concept = conceptRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException(List.of("Concept not found")));

        concept.setName(dto.getName());
        concept.setMeaning(dto.getMeaning());
        concept.setAttrs(dto.getAttrs());
        concept.setKeyphrases(dto.getKeyphrases());
        concept.setSimilar(dto.getSimilar());

        Concept updated = conceptRepository.save(concept);

        return new ConceptDto(
                updated.getId(),
                updated.getName(),
                updated.getMeaning(),
                updated.getAttrs(),
                updated.getKeyphrases(),
                updated.getSimilar()
        );
    }

    @Override
    public RelationDto updateRelation(String id, RelationDto dto) {
        Relation relation = relationRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException(List.of("Relation not found")));

        relation.setName(dto.getName());
        relation.setMeaning(dto.getMeaning());
        relation.setSimilar(dto.getSimilar());
        relation.setKeyword(dto.getKeyword());

        Relation updated = relationRepository.save(relation);

        return new RelationDto(
                updated.getId(),
                updated.getName(),
                updated.getMeaning(),
                updated.getSimilar(),
                updated.getKeyword()
        );
    }

    @Override
    public RelationDto createRelation(RelationDto dto) {
        // Kiểm tra nếu đã tồn tại ID (nếu bạn không tự sinh ID)
        relationRepository.findByName(dto.getName())
                .orElseThrow(() -> new InputInvalidException(List.of("Relation already exists")));


        Relation relation = new Relation();
        relation.setName(dto.getName());
        relation.setMeaning(dto.getMeaning());
        relation.setSimilar(dto.getSimilar());
        relation.setKeyword(dto.getKeyword());

        Relation saved = relationRepository.save(relation);

        return new RelationDto(
                saved.getId(),
                saved.getName(),
                saved.getMeaning(),
                saved.getSimilar(),
                saved.getKeyword()
        );
    }

    @Override
    public ConceptDto createConcept(ConceptDto dto) {
        // Kiểm tra nếu đã tồn tại ID (nếu bạn không tự sinh ID)
        conceptRepository.findByName(dto.getName())
                .orElseThrow(() -> new InputInvalidException(List.of("Concept already exists")));

        Concept concept = new Concept();
        concept.setName(dto.getName());
        concept.setMeaning(dto.getMeaning());
        concept.setAttrs(dto.getAttrs());
        concept.setKeyphrases(dto.getKeyphrases());
        concept.setSimilar(dto.getSimilar());

        Concept saved = conceptRepository.save(concept);

        return new ConceptDto(
                saved.getId(),
                saved.getName(),
                saved.getMeaning(),
                saved.getAttrs(),
                saved.getKeyphrases(),
                saved.getSimilar()
        );
    }
}
