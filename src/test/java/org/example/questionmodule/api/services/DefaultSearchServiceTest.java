package org.example.questionmodule.api.services;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import org.example.questionmodule.api.entities.*;
import org.example.questionmodule.api.repositories.ConceptRepository;
import org.example.questionmodule.api.repositories.RelationRepository;
import org.example.questionmodule.api.repositories.TripletRepository;
import org.example.questionmodule.api.services.interfaces.UtilService;
import org.example.questionmodule.api.services.mapper.GraphKnowledgeMapper;
import org.example.questionmodule.api.services.mapper.TripletMapper;
import org.example.questionmodule.rule.RuleParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class DefaultSearchServiceTest {
    @InjectMocks
    private DefaultSearchService defaultSearchService;

    @Mock
    private VNCoreNlpService vnCoreNlpService;
    @Mock
    private ConceptRepository conceptRepository;
    @Mock
    private RelationRepository relationRepository;
    @Mock
    private TripletRepository tripletRepository;
    @Mock
    private RuleParser ruleParser;
    @Mock
    private AIService aiService;
    @Mock
    private TripletMapper tripletMapper;
    @Mock
    private GraphKnowledgeMapper graphKnowledgeMapper;
    @Mock
    private UtilService utilService;



    private Concept createConcept(String name) {
        Concept c = new Concept();
        c.setName(name);
        return c;
    }

    private Relation createRelation(String name, Set<Concept> subjects, Set<Concept> objects) {
        Relation r = new Relation();
        r.setName(name);
        r.setConceptSubjects(subjects);
        r.setConceptObjects(objects);
        return r;
    }

    @Test
    void testFullTriplet() {
        Concept subject = createConcept("Xe");
        Concept object = createConcept("trên đường");
        Relation relation = createRelation("chạy", Set.of(subject), Set.of(object));

        Multimap<Integer, Concept> concepts = ArrayListMultimap.create();

        concepts.put(0, subject);
        concepts.put(2, object);

        Multimap<Integer, Relation> relations = ArrayListMultimap.create();
        relations.put(1, relation);
        List<Triplet> result = defaultSearchService.getTriplet(concepts, relations, SentenceType.AFFIRMATIVE);

        assertEquals(1, result.size());
        Triplet t = result.get(0);
        assertEquals("Xe", t.getSubject().getName());
        assertEquals("chạy", t.getRelation().getName());
        assertEquals("trên đường", t.getObject().getName());
    }
//
    @Test
    void testTripletOnlySubject() {
        Concept subject = createConcept("Xe");
        Relation relation = createRelation("chạy", Set.of(subject), Set.of());

        Multimap<Integer, Concept> concepts = ArrayListMultimap.create();

        concepts.put(
                0, subject
        );
        Multimap<Integer, Relation> relations = ArrayListMultimap.create();

        relations.put(1, relation);

        List<Triplet> result = defaultSearchService.getTriplet(concepts, relations, SentenceType.AFFIRMATIVE);

        assertEquals(1, result.size());
        assertEquals("Xe", result.get(0).getSubject().getName());
        assertNull(result.get(0).getObject());
    }
//
    @Test
    void testTripletOnlyObject() {
        Concept object = createConcept("trên đường");
        Relation relation = createRelation("chạy", Set.of(), Set.of(object));

        Multimap<Integer, Concept> concepts = ArrayListMultimap.create();
        concepts.put(2, object);

        Multimap<Integer, Relation> relations = ArrayListMultimap.create();
        relations.put(1, relation);

        List<Triplet> result = defaultSearchService.getTriplet(concepts, relations, SentenceType.AFFIRMATIVE);

        assertEquals(1, result.size());
        assertNull(result.get(0).getSubject());
        assertEquals("trên đường", result.get(0).getObject().getName());
    }
//
    @Test
    void testNoTripletFound() {
        Relation relation = createRelation("chạy", Set.of(), Set.of());

        Multimap<Integer, Concept> concepts = ArrayListMultimap.create();

        Multimap<Integer, Relation> relations = ArrayListMultimap.create();
        relations.put(1, relation);

        List<Triplet> result = defaultSearchService.getTriplet(concepts, relations, SentenceType.AFFIRMATIVE);

        assertTrue(result.isEmpty());
    }

    private Triplet createTriplet(String tid, GraphKnowledge... graphs) {
        Triplet triplet = new Triplet();
        triplet.setId(tid);
        triplet.setRelation(new Relation());
        triplet.setSubject(new Concept());
        triplet.setObject(new Concept());

        List<TripletGraph> tgs = new ArrayList<>();
        for (GraphKnowledge g : graphs) {
            TripletGraph tg = new TripletGraph();
            tg.setTriplet(triplet);
            tg.setGraphKnowledge(g);
            tg.setIsRoot(true);
            tg.setId(new TripletGraphId(tid, g.getId()));
            tgs.add(tg);

            // Gắn vào graph
            if (g.getTripletGraphs() == null) {
                g.setTripletGraphs(new ArrayList<>());
            }
            g.getTripletGraphs().add(tg);
        }

        // Set ngược lại cho triplet
        triplet.setTripletGraphs(tgs);

        return triplet;
    }


    private GraphKnowledge createGraph(String id) {
        GraphKnowledge graph = new GraphKnowledge();
        graph.setId(id);
        graph.setTripletGraphs(new ArrayList<>());
        return graph;
    }

    @Test
    void testSingleGraphMatchesAllTriplets() {
        GraphKnowledge graphA = createGraph("A");

        Triplet t1 = createTriplet("T1", graphA);
        Triplet t2 = createTriplet("T2", graphA);
        Triplet t3 = createTriplet("T3", graphA);

        List<GraphKnowledge> result = defaultSearchService.findMatchingGraphs(List.of(t1, t2, t3), List.of(graphA));

        assertEquals(1, result.size());
        assertEquals("A", result.get(0).getId());
    }

    @Test
    void testMultipleGraphsDifferentMatchCounts() {
        GraphKnowledge graphA = createGraph("A");
        GraphKnowledge graphB = createGraph("B");

        Triplet t1 = createTriplet("T1", graphA);
        Triplet t2 = createTriplet("T2", graphA, graphB);
        Triplet t3 = createTriplet("T3", graphB);

        List<GraphKnowledge> result = defaultSearchService.findMatchingGraphs(List.of(t1, t2, t3), List.of(graphA, graphB));

        assertEquals(2, result.size());
        List<String> ids = result.stream().map(GraphKnowledge::getId).toList();
        assertTrue(ids.contains("A"));
        assertTrue(ids.contains("B"));
    }

    @Test
    void testNoMatchingGraphs() {
        // Tạo GraphKnowledge A
        GraphKnowledge graphA = createGraph("A");

        // Tạo các Triplet mà không có GraphKnowledge (rỗng)
        Triplet t1 = new Triplet();
        t1.setId(UUID.randomUUID().toString());
        t1.setTripletGraphs(new ArrayList<>());  // Đảm bảo tripletGraphs là danh sách rỗng

        Triplet t2 = new Triplet();
        t2.setId(UUID.randomUUID().toString());
        t2.setTripletGraphs(new ArrayList<>());  // Đảm bảo tripletGraphs là danh sách rỗng

        Triplet t3 = createTriplet("T3", graphA);

        // Tìm GraphKnowledge nào khớp với các Triplet (danh sách rỗng, không có GraphKnowledge)
        List<GraphKnowledge> result = defaultSearchService.findMatchingGraphs(List.of(t1, t2), List.of(graphA));

        result.forEach(r -> System.out.println(r.getId()));
        // Kiểm tra kết quả là rỗng
        assertTrue(result.isEmpty(), "Expected no matching graphs, but found some.");
    }

    @Test
    void testReturnMultipleGraphsIfEqualTopCount() {
        GraphKnowledge graphA = createGraph("A");
        GraphKnowledge graphB = createGraph("B");

        Triplet t1 = createTriplet("T1", graphA, graphB);
        Triplet t2 = createTriplet("T2", graphA, graphB);

        List<GraphKnowledge> result = defaultSearchService.findMatchingGraphs(List.of(t1, t2), List.of(graphA, graphB));

        assertEquals(2, result.size());
        List<String> ids = result.stream().map(GraphKnowledge::getId).toList();
        assertTrue(ids.contains("A"));
        assertTrue(ids.contains("B"));
    }

}