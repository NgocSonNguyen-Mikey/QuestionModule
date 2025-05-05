package org.example.questionmodule.api.services;

import com.google.common.collect.Multimap;
import lombok.RequiredArgsConstructor;
import org.example.questionmodule.api.dtos.*;
import org.example.questionmodule.api.entities.*;
import org.example.questionmodule.api.repositories.ConceptRepository;
import org.example.questionmodule.api.repositories.GraphKnowledgeRepository;
import org.example.questionmodule.api.repositories.RelationRepository;
import org.example.questionmodule.api.repositories.TripletRepository;
import org.example.questionmodule.api.services.interfaces.SearchService;
import org.example.questionmodule.api.services.interfaces.UtilService;
import org.example.questionmodule.api.services.mapper.*;
import org.example.questionmodule.rule.RuleParser;
import org.springframework.stereotype.Service;
import vn.pipeline.Sentence;
import vn.pipeline.Word;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class DefaultSearchService implements SearchService {
    private final VNCoreNlpService vnCoreNlpService;
    private final ConceptRepository conceptRepository;
    private final RelationRepository relationRepository;
    private final TripletRepository tripletRepository;
    private final RuleParser ruleParser;
    private final AIService aiService;
    private final TripletMapper tripletMapper;
    private final GraphKnowledgeRepository graphKnowledgeRepository;
    private final UtilService utilService;
    private final ArticleMapper articleMapper;
    private final ClauseMapper clauseMapper;
    private final PointMapper pointMapper;

    @Override
    public ResponseDto process(String sentences) {
//        sentences = sentences.toLowerCase();
        long start = System.currentTimeMillis();
        List<String> sentencePretreatment = aiService.pretreatmentAnswer(sentences);
        List<Concept> concepts = conceptRepository.findAllQuery();
        List<Relation> relations = relationRepository.findAllQuery();
        List<Triplet> triplets = tripletRepository.findAllQuery();;
        List<GraphKnowledge> graphKnowledges = graphKnowledgeRepository.findAllQuery();
        System.out.println("Query Triplet took: " + (System.currentTimeMillis() - start) + "ms");

        List<Sentence> sentenceList = sentencePretreatment.stream()
                .map(vnCoreNlpService::analyzeText)
                .flatMap(List::stream)
                .toList();

        List<Triplet> findTriplet = sentenceList.stream().map(sentence -> {
            try {
                return processSentence(sentence, concepts, relations, triplets);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).flatMap(List::stream).toList();

        List<GraphKnowledge> findGraph = findMatchingGraphs(findTriplet, graphKnowledges);
        return ResponseDto
                .builder()
                .question(sentences)
                .answer(
                        aiService.getAnswer(sentences,findGraph.stream()
                                        .map(graph -> {
                                            StringBuilder builder = new StringBuilder();

                                            if (graph.getPoint() != null) {
                                                Point point = graph.getPoint();
                                                Clause clause = point.getClause();
                                                Article article = clause.getArticle();
                                                Chapter chapter = article.getChapter();
                                                Law law = chapter.getLaw();

                                                builder.append("Luật: ").append(law.getName()).append("\n")
                                                        .append("Chương: ").append(chapter.getCode()).append(" - ").append(chapter.getContent()).append("\n")
                                                        .append("Điều ").append(article.getCode()).append(": ").append(article.getTitle()).append("\n")
                                                        .append("Khoản ").append(clause.getCode()).append(": ").append(clause.getContent()).append("\n")
                                                        .append("Điểm ").append(point.getCode()).append(": ").append(point.getContent());
                                            } else if (graph.getClause() != null) {
                                                Clause clause = graph.getClause();
                                                Article article = clause.getArticle();
                                                Chapter chapter = article.getChapter();
                                                Law law = chapter.getLaw();

                                                builder.append("Luật: ").append(law.getName()).append("\n")
                                                        .append("Chương: ").append(chapter.getCode()).append(" - ").append(chapter.getContent()).append("\n")
                                                        .append("Điều ").append(article.getCode()).append(": ").append(article.getTitle()).append("\n")
                                                        .append("Khoản ").append(clause.getCode()).append(": ").append(clause.getContent());
                                            } else if (graph.getArticle() != null) {
                                                Article article = graph.getArticle();
                                                Chapter chapter = article.getChapter();
                                                Law law = chapter.getLaw();

                                                builder.append("Luật: ").append(law.getName()).append("\n")
                                                        .append("Chương: ").append(chapter.getCode()).append(" - ").append(chapter.getContent()).append("\n")
                                                        .append("Điều ").append(article.getCode()).append(": ").append(article.getTitle());

                                                if (article.getContent() != null && !article.getContent().isBlank()) {
                                                    builder.append("\n").append(article.getContent());
                                                }
                                            }

                                            return builder.toString();
                                        })
                                        .filter(content -> content != null && !content.isBlank())
                                        .collect(Collectors.joining("\n\n"))
                        ))
                .attachedLaw(mapToLawDtoList(findGraph))
                .build();
    }

    private List<Triplet> processSentence(Sentence sentence,  List<Concept> concepts, List<Relation> relations, List<Triplet> triplets) throws IOException {
        List<Word> Word = sentence.getWords().stream().toList();
        Multimap<Integer, List<Word>> subjects = utilService.deleteDuplicateWord(ruleParser.executeRules(Word, "subject_rule.drl"));
        Multimap<Integer, List<Word>> relationMap = utilService.deleteDuplicateWord(ruleParser.executeRules(Word, "relation_rule.drl"));
        System.out.println("cau:" + sentence.toString());
        SentenceType sentenceType = ruleParser.executeSentenceTypeRules(Word);

        Multimap<Integer, Concept> conceptList = utilService.getConcept(concepts, subjects);
        Multimap<Integer, Relation> relationList = utilService.getRelation(relations, relationMap);

        System.out.println("Concept List:");
        for (Map.Entry<Integer, List<Word>> entry : subjects.entries()) {
            Integer key = entry.getKey();
            List<Word> value = entry.getValue();
            System.out.println("Key: " + key + ", Value: " + value.stream()
                    .map(vn.pipeline.Word::getForm)
                    .collect(Collectors.joining(" ")));
        }

        System.out.println("Relation List:");
        for (Map.Entry<Integer, List<Word>> entry : relationMap.entries()) {
            Integer key = entry.getKey();
            List<Word> value = entry.getValue();
            System.out.println("Key: " + key + ", Value: " + value.stream()
                    .map(vn.pipeline.Word::getForm)
                    .collect(Collectors.joining(" ")));

        }

        List<Triplet> tripletList = getTriplet(conceptList, relationList, sentenceType);
        return triplets.stream()
                .filter(t -> tripletList.stream().anyMatch(triplet -> {
                    boolean matchesSubjectRelationObject = false;
                    boolean matchesSubjectRelation = false;
                    boolean matchesRelationObject = false;
                    boolean matchesSubjectObject = false;
                            if(t.getObject() == null){
                                matchesSubjectRelation =
                                        triplet.getSubject() != null && triplet.getRelation() != null &&
                                                triplet.getSubject().getId().equals(t.getSubject().getId()) &&
                                                triplet.getRelation().getId().equals(t.getRelation().getId()) &&
                                                triplet.getType().equals(t.getType());
                            }else {
                                if (t.getSubject() == null){
                                    matchesRelationObject =
                                            triplet.getRelation() != null && triplet.getObject() != null &&
                                                    triplet.getRelation().getId().equals(t.getRelation().getId()) &&
                                                    triplet.getObject().getId().equals(t.getObject().getId()) &&
                                                    triplet.getType().equals(t.getType());
                                }
                                else {
                                    matchesSubjectRelationObject =
                                            triplet.getSubject() != null && triplet.getObject() != null &&
                                                    triplet.getSubject().getId().equals(t.getSubject().getId()) &&
                                                    triplet.getObject().getId().equals(t.getObject().getId()) &&
                                                    triplet.getRelation().getId().equals(t.getRelation().getId()) &&
                                                    triplet.getType().equals(t.getType());

                                    matchesSubjectRelation =
                                            triplet.getSubject() != null && triplet.getRelation() != null &&
                                                    triplet.getSubject().getId().equals(t.getSubject().getId()) &&
                                                    triplet.getRelation().getId().equals(t.getRelation().getId()) &&
                                                    triplet.getType().equals(t.getType());

                                    matchesRelationObject =
                                            triplet.getRelation() != null && triplet.getObject() != null &&
                                                    triplet.getRelation().getId().equals(t.getRelation().getId()) &&
                                                    triplet.getObject().getId().equals(t.getObject().getId()) &&
                                                    triplet.getType().equals(t.getType());

                                    matchesSubjectObject =
                                            triplet.getSubject() != null && triplet.getObject() != null &&
                                                    triplet.getSubject().getId().equals(t.getSubject().getId()) &&
                                                    triplet.getObject().getId().equals(t.getObject().getId()) &&
                                                    triplet.getType().equals(t.getType());
                                }
                            }
                    // Kiểm tra nếu là bộ ba hoặc bộ hai (subject, relation hoặc relation, object)
                    return matchesSubjectRelationObject || matchesSubjectRelation || matchesRelationObject || matchesSubjectObject;
                }))
                .toList();
    }

    public List<Triplet> getTriplet(Multimap<Integer, Concept> concepts, Multimap<Integer, Relation> relations, SentenceType sentenceType) {
        List<Triplet> triplets = new ArrayList<>();

        // Duyệt qua các Relation
        for (Map.Entry<Integer, Relation> entry : relations.entries()) {
            int relationIndex = entry.getKey();
            Relation relation = entry.getValue();
            System.out.println(relation.getName());
            // Tìm subject và object (chỉ cần lặp qua concepts một lần)
            Concept subject = concepts.entries().stream()
                    .filter(conceptEntry -> conceptEntry.getKey() < relationIndex )//&& relation.getConceptSubjects().contains(conceptEntry.getValue())
                    .map(Map.Entry::getValue)
                    .findFirst().orElse(null);

            Concept object = concepts.entries().stream()
                    .filter(conceptEntry -> conceptEntry.getKey() > relationIndex )//&& relation.getConceptObjects().contains(conceptEntry.getValue())
                    .map(Map.Entry::getValue)
                    .findFirst().orElse(null);

            // Nếu tìm thấy subject và object thì tạo bộ ba
            if (subject != null && object != null) {
                System.out.println(subject.getName() + " " + relation.getName() + " " + object.getName() + " " + sentenceType);
                triplets.add(Triplet.builder()
                        .subject(subject)
                        .object(object)
                        .relation(relation)
                        .type(sentenceType)
                        .build());
            }
            // Nếu không tìm thấy bộ ba, ghép bộ hai subject và relation
            else if (subject != null) {
                System.out.println(subject.getName() + " " + relation.getName() + " (No object)" + " " + sentenceType);
                triplets.add(Triplet.builder()
                        .subject(subject)
                        .relation(relation)
                        .type(sentenceType)
                        .build());
            }
            // Nếu không tìm thấy bộ ba, ghép bộ hai relation và object
            else if (object != null) {
                System.out.println(relation.getName() + " " + object.getName() + " (No subject)" + " " + sentenceType);
                triplets.add(Triplet.builder()
                        .object(object)
                        .relation(relation)
                        .type(sentenceType)
                        .build());
            }
        }

        return triplets;
    }
    public List<GraphKnowledge> findMatchingGraphs(List<Triplet> tripletList, List<GraphKnowledge> graphKnowledges) {
        // Tạo Map đếm số lần xuất hiện của từng GraphKnowledge
        Map<GraphKnowledge, Integer> graphCount = new HashMap<>();
        List<GraphKnowledge> result = new ArrayList<>() ;

        // Duyệt từng bộ ba để lấy GraphKnowledge của nó
        for (Triplet triplet : tripletList) {
            List<GraphKnowledge> graphRoot = triplet.getTripletGraphs().stream()
                    .filter(TripletGraph::getIsRoot)
                    .toList().stream()
                    .map(TripletGraph::getGraphKnowledge)
                    .toList();

            result.addAll(graphRoot);

            for (GraphKnowledge graph : triplet.getTripletGraphs().stream()
                    .map(TripletGraph::getGraphKnowledge)
                    .toList()
            ) {
                graphCount.put(graph, graphCount.getOrDefault(graph, 0) + 1);
            }
        }

        // Sắp xếp theo số bộ ba khớp nhiều nhất
        List<Map.Entry<GraphKnowledge, Integer>> sortedGraphs = graphCount.entrySet()
                .stream()
                .sorted((a, b) -> Integer.compare(b.getValue(), a.getValue())) // Sắp xếp giảm dần theo số lượng bộ ba khớp
                .toList();

        // Tìm tập hợp Graph Knowledge phù hợp nhất
        int temp = result.size();
        for (int i = tripletList.size(); i > 0; i--) {
            Integer count = i;
            result = sortedGraphs.stream()
                    .filter(entry -> entry.getValue() >= count)
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toList());

            if (result.size() >= temp + 3) {
                break; // Trả về danh sách GraphKnowledge tìm được
            }
        }

        Set<Triplet> inputTriplets = new HashSet<>(tripletList);

        for (GraphKnowledge graph : graphKnowledges) {
            if (!result.contains(graph)) {
                Set<Triplet> graphTriplets = graph.getTripletGraphs().stream().map(TripletGraph::getTriplet).collect(Collectors.toSet());
                if (inputTriplets.containsAll(graphTriplets)) {
                    result.add(graph);
                }
            }
        }

        return result; // Nếu không tìm thấy Graph nào chứa bộ ba, trả về danh sách rỗng
    }

    public List<LawDto> mapToLawDtoList(List<GraphKnowledge> graphKnowledgeList) {
        Map<String, LawDto> lawDtoMap = new HashMap<>();

        for (GraphKnowledge gk : graphKnowledgeList) {

            Law law;

            Article article = gk.getArticle();
            Clause clause = gk.getClause();
            Point point = gk.getPoint();

            if (article != null && article.getChapter().getLaw() != null) {
                System.out.println("graph Id: " + gk.getId());
                law = article.getChapter().getLaw();
            } else if (clause != null && clause.getArticle() != null && clause.getArticle().getChapter().getLaw() != null) {
                System.out.println("graph Id: " + gk.getId());
                law = clause.getArticle().getChapter().getLaw();
            } else if (point != null && point.getClause() != null && point.getClause().getArticle() != null
                    && point.getClause().getArticle().getChapter().getLaw() != null) {
                System.out.println("graph Id: " + gk.getId());
                law = point.getClause().getArticle().getChapter().getLaw();
            } else {
                law = null;
            }

            if (law == null) continue;

            // Dùng id để gom theo từng Law
            LawDto dto = lawDtoMap.computeIfAbsent(law.getId(), id -> {
                LawDto newDto = new LawDto();
                newDto.name = law.getName();
                newDto.year = law.getYear();
                newDto.articles = new ArrayList<>();
                newDto.clauses = new ArrayList<>();
                newDto.points = new ArrayList<>();
                return newDto;
            });

            if (article != null) {
                ArticleDto articleDto = articleMapper.toDto(article);
                if (!dto.articles.contains(articleDto)) {
                    dto.articles.add(articleDto);
                }
            }

            if (clause != null) {
                ClauseDto clauseDto = clauseMapper.toDto(clause);
                if (!dto.clauses.contains(clauseDto)) {
                    dto.clauses.add(clauseDto);
                }
            }

            if (point != null) {
                PointDto pointDto = pointMapper.toDto(point);
                if (!dto.points.contains(pointDto)) {
                    dto.points.add(pointDto);
                }
            }
        }

        return new ArrayList<>(lawDtoMap.values());
    }


}
