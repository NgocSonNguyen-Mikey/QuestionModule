package org.example.questionmodule.api.services;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import org.example.questionmodule.api.entities.Concept;
import org.example.questionmodule.api.entities.Relation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import vn.pipeline.Word;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class DefaultUtilServiceTest {
    @InjectMocks
    private  DefaultUtilService defaultUtilService;



    //Match relation test
    @Test
    void testMatchRelationExact() {
        Relation relation = new Relation();
        relation.setName("equal");
        relation.setSimilar(Set.of("similar", "related"));
        relation.setKeyword(Set.of("keyword"));

        assertTrue(defaultUtilService.matchRelation(relation, "equal"));  // Expected true
    }

    @Test
    void testMatchRelationSimilar() {
        Relation relation = new Relation();
        relation.setName("equal");
        relation.setSimilar(Set.of("similar", "related"));
        relation.setKeyword(Set.of("keyword"));

        assertTrue(defaultUtilService.matchRelation(relation, "similar"));  // Expected true
    }

    @Test
    void testNoMatchRelation() {
        Relation relation = new Relation();
        relation.setName("equal");
        relation.setSimilar(Set.of("similar", "related"));
        relation.setKeyword(Set.of("keyword"));

        assertFalse(defaultUtilService.matchRelation(relation, "notmatch"));  // Expected false
    }

    //Match concept
    @Test
    void testMatchConceptExact() {
        Concept concept = new Concept();
        concept.setName("law");
        concept.setSimilar(Set.of("rule", "act"));
        concept.setKeyphrases(Set.of("legal term"));

        assertTrue(defaultUtilService.matchConcept(concept, "law"));  // Expected true
    }

    @Test
    void testMatchConceptSimilar() {
        Concept concept = new Concept();
        concept.setName("law");
        concept.setSimilar(Set.of("rule", "act"));
        concept.setKeyphrases(Set.of("legal term"));

        assertTrue(defaultUtilService.matchConcept(concept, "rule"));  // Expected true
    }

    @Test
    void testMatchConceptKeyphrase() {
        Concept concept = new Concept();
        concept.setName("law");
        concept.setSimilar(Set.of("rule", "act"));
        concept.setKeyphrases(Set.of("legal term"));

        assertTrue(defaultUtilService.matchConcept(concept, "legal term"));  // Expected true
    }

    @Test
    void testNoMatchConcept() {
        Concept concept = new Concept();
        concept.setName("law");
        concept.setSimilar(Set.of("rule", "act"));
        concept.setKeyphrases(Set.of("legal term"));

        assertFalse(defaultUtilService.matchConcept(concept, "policy"));  // Expected false
    }

    //getConcept
    @Test
    void testGetConcept() {
        // Tạo đối tượng Concept
        Concept concept1 = new Concept();
        concept1.setName("law");

        Concept concept2 = new Concept();
        concept2.setName("rule");

        // Tạo danh sách Word (giả lập)
        List<Word> words1 = List.of(new Word(0, "law", "N"));
        List<Word> words2 = List.of(new Word(1, "rule", "N"));
        Multimap<Integer, List<Word>> words = ArrayListMultimap.create();
        words.put(0, words1);
        words.put(1, words2);

        List<Concept> concepts = List.of(concept1, concept2);

        Multimap<Integer, Concept> result = defaultUtilService.getConcept(concepts, words);

        // Kiểm tra kết quả
        assertTrue(result.containsEntry(0, concept1));
        assertTrue(result.containsEntry(1, concept2));
    }

    @Test
    void testGetRelation() {
        // Tạo đối tượng Relation
        Relation relation1 = new Relation();
        relation1.setName("equal");

        Relation relation2 = new Relation();
        relation2.setName("similar");

        // Tạo danh sách Word (giả lập)
        List<Word> words1 = List.of(new Word(0, "equal", "V"));   // Dùng constructor đúng
        List<Word> words2 = List.of(new Word(1, "similar", "V"));
        Multimap<Integer, List<Word>> words = ArrayListMultimap.create();
        words.put(0, words1);
        words.put(1, words2);

        List<Relation> relations = List.of(relation1, relation2);

        Multimap<Integer, Relation> result = defaultUtilService.getRelation(relations, words);

        // Kiểm tra kết quả
        assertTrue(result.containsEntry(0, relation1));
        assertTrue(result.containsEntry(1, relation2));
    }
}
