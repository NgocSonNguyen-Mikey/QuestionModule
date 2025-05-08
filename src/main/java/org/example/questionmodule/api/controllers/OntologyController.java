package org.example.questionmodule.api.controllers;

import lombok.RequiredArgsConstructor;
import org.example.questionmodule.api.dtos.admin.ConceptDto;
import org.example.questionmodule.api.dtos.admin.RelationDto;
import org.example.questionmodule.api.services.interfaces.OntologyService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ontology")
@RequiredArgsConstructor
public class OntologyController {
    private final OntologyService ontologyService;

    @GetMapping("/concept")
    public ResponseEntity<Page<ConceptDto>> getConcept(
            @RequestParam int page,
            @RequestParam int size){
        return ResponseEntity.ok(ontologyService.getAllConcept(page, size));
    }

    @GetMapping("/relation")
    public ResponseEntity<Page<RelationDto>> getRelation(
            @RequestParam int page,
            @RequestParam int size){
        return ResponseEntity.ok(ontologyService.getAllRelation(page, size));
    }

    @PutMapping("/relation/{id}")
    public ResponseEntity<RelationDto> updateRelation(
            @PathVariable String id,
            @RequestBody RelationDto dto) {
        RelationDto updated = ontologyService.updateRelation(id, dto);
        return ResponseEntity.ok(updated);
    }

    @PutMapping("/concept/{id}")
    public ResponseEntity<ConceptDto> updateConcept(
            @PathVariable String id,
            @RequestBody ConceptDto dto) {
        ConceptDto updated = ontologyService.updateConcept(id, dto);
        return ResponseEntity.ok(updated);
    }

    @PostMapping("concept")
    public ResponseEntity<ConceptDto> createConcept(@RequestBody ConceptDto dto) {
        ConceptDto created = ontologyService.createConcept(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PostMapping("relation")
    public ResponseEntity<RelationDto> createRelation(@RequestBody RelationDto dto) {
        RelationDto created = ontologyService.createRelation(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
}
