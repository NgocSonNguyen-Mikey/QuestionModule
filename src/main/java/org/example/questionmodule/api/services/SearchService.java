package org.example.questionmodule.api.services;

import lombok.RequiredArgsConstructor;
import org.antlr.v4.runtime.misc.Triple;
import org.example.questionmodule.api.entities.Concept;
import org.example.questionmodule.api.entities.Relation;
import org.example.questionmodule.api.entities.Triplet;
import org.example.questionmodule.api.repositories.ConceptRepository;
import org.example.questionmodule.api.repositories.RelationRepository;
import org.example.questionmodule.api.repositories.TripletRepository;
import org.example.questionmodule.rule.RuleParser;
import org.springframework.stereotype.Service;
import vn.pipeline.Sentence;
import vn.pipeline.Word;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class SearchService {
    private final VNCoreNlpService vnCoreNlpService;
    private final ConceptRepository conceptRepository;
    private final RelationRepository relationRepository;
    private final TripletRepository tripletRepository;
    private final RuleParser ruleParser;

    private Optional<Concept> getConcept(List<Concept> concepts, String word){
        Optional<Concept> foundConcept = concepts.stream()
                .filter(concept -> concept.getKeyphrases().stream().anyMatch(key -> key.contains(word)))
                .findFirst();
        if (foundConcept.isEmpty())
            foundConcept = concepts.stream()
                    .filter(concept -> concept.getSimilar().stream().anyMatch(key -> key.contains(word)))
                    .findFirst();

        return foundConcept;
    }

    private Optional<Relation> getRelation(List<Relation> relations, String word){
        return relations.stream()
                .filter(relation -> relation.getSimilar().stream().anyMatch(key -> key.contains(word)))
                .findFirst();
    }


    public List<String> process(String sentences) throws IOException {
        List<Concept> concepts = conceptRepository.findAll();
        List<Relation> relations = relationRepository.findAll();

        List<Sentence> sentenceList = vnCoreNlpService.analyzeText(sentences);
        List<Word> relationWord = sentenceList.stream()
                .flatMap(sentence -> sentence.getWords().stream())
                .toList();
        List<String> subjects = ruleParser.executeRules(relationWord, "subject_rule.drl");
        subjects.forEach(s -> System.out.print(s+"\t"));
        return subjects;
    }

//    public List<Triplet> getGraph(){
//        var objectId = "b24f0721-eddf-11ef-9d94-0242ac1a0002";
//        var subjectId = "%";
//        var relationId = "6f72a86f-ede0-11ef-9d94-0242ac1a0002";
//
//        return tripletRepository.getGraphFromTriplet(subjectId,objectId,relationId);
//    }
}
