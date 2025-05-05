package org.example.questionmodule.api.services;

import lombok.RequiredArgsConstructor;
import org.example.questionmodule.api.entities.Concept;
import org.example.questionmodule.api.entities.GraphKnowledge;
import org.example.questionmodule.api.entities.Relation;
import org.example.questionmodule.api.entities.Triplet;
import org.example.questionmodule.api.repositories.ConceptRepository;
import org.example.questionmodule.api.repositories.RelationRepository;
import org.example.questionmodule.api.repositories.TripletRepository;
import org.example.questionmodule.api.services.interfaces.OntologyService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DefaultOntologyService implements OntologyService {

    private final ConceptRepository conceptRepository;
    private final RelationRepository relationRepository;
    private final TripletRepository tripletRepository;

    public void test(){

    }
}
