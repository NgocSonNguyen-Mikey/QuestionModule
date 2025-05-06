package org.example.questionmodule.api.services;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import lombok.RequiredArgsConstructor;
import org.example.questionmodule.api.entities.*;
import org.example.questionmodule.api.repositories.*;
import org.example.questionmodule.api.services.interfaces.UtilService;
import org.example.questionmodule.rule.RuleParser;
import org.springframework.ai.document.Document;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.pipeline.Sentence;
import vn.pipeline.Word;

import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class AsyncService {

    private final Word2VecService word2VecService;
    private final LawRepository lawRepository;
    private final VNCoreNlpService vnCoreNlpService;
    private final UtilService utilService;
    private final AIService aiService;
    private final ConceptRepository conceptRepository;
    private final RelationRepository relationRepository;
    private final TripletRepository tripletRepository;
    private final RuleParser ruleParser;
    private final GraphKnowledgeRepository graphKnowledgeRepository;
    private final TripletGraphRepository tripletGraphRepository;

    @Transactional
    @Async
    public void saveToDatabase(List<Document> documents) {
        if (documents.isEmpty()) return ;

        Document first = documents.get(0);
        String lawTitle = (String) first.getMetadata().get("lawTitle");
        String lawNumber = (String) first.getMetadata().get("lawNumber");
        String[] parts = lawNumber.split("/");
        int year = Integer.parseInt(parts.length >= 2 ? parts[1] : "");

        lawRepository.findById(lawNumber).ifPresent(lawRepository::delete);

        Law law = new Law();
        law.setId(lawNumber.replace("/", "-"));
        law.setName(lawTitle + " - Số: " + lawNumber);
        law.setYear(Date.valueOf(LocalDate.of(year, 1, 1)));

        // Khởi tạo danh sách
        List<Chapter> chapters = new ArrayList<>();
        List<Article> articles = new ArrayList<>();
        List<Clause> clauses = new ArrayList<>();
        List<Point> points = new ArrayList<>();

        Map<String, Chapter> chapterMap = new HashMap<>();
        Map<String, Article> articleMap = new HashMap<>();
        Map<String, Clause> clauseMap = new HashMap<>();

        for (Document doc : documents) {
            Map<String, Object> meta = doc.getMetadata();
            String chapterCode = (String) meta.get("chapterCode");
            String chapterTitle = (String) meta.get("chapterTitle");
            String articleCode = (String) meta.get("article");
            String articleTitle = (String) meta.get("title");
            String clauseCode = (String) meta.get("clause");
            String pointCode = (String) meta.get("point");

            Chapter chapter = chapterMap.computeIfAbsent(chapterCode, code -> {
                Chapter ch = new Chapter();
                ch.setCode(code);
                ch.setContent(chapterTitle);
                ch.setLaw(law);
                chapters.add(ch);
                return ch;
            });

            String articleKey = chapterCode + "-" + articleCode;
            Article article = articleMap.computeIfAbsent(articleKey, key -> {
                Article a = new Article();
                a.setCode(articleCode);
                a.setTitle(articleTitle);
                a.setChapter(chapter);
                articles.add(a);
                return a;
            });

            if ((boolean) meta.getOrDefault("fullArticle", false)) {
                article.setContent((String) meta.get("content"));
                continue;
            }

            String clauseKey = articleKey + "-" + clauseCode;
            Clause clause = clauseMap.computeIfAbsent(clauseKey, key -> {
                Clause c = new Clause();
                c.setCode(clauseCode);
                c.setContent((String) meta.get("clauseContent"));
                c.setArticle(article);
                clauses.add(c);
                return c;
            });

            if (pointCode != null) {
                Point point = new Point();
                point.setCode(pointCode);
                point.setContent((String) meta.get("pointContent"));
                point.setClause(clause);
                points.add(point);
            }
        }

        // Gán quan hệ cha - con
        for (Chapter ch : chapters)
            ch.setArticles(articles.stream().filter(a -> a.getChapter().equals(ch)).collect(Collectors.toList()));
        for (Article a : articles)
            a.setClauses(clauses.stream().filter(c -> c.getArticle().equals(a)).collect(Collectors.toList()));
        for (Clause c : clauses)
            c.setPoints(points.stream().filter(p -> p.getClause().equals(c)).collect(Collectors.toList()));

        law.setChapters(chapters);
        Law saveLaw = lawRepository.save(law);
        addGraphsForLaw(saveLaw);

    }

    public void addGraphsForLaw(Law law) {
        List<Concept> concepts = conceptRepository.findAllQuery();
        List<Relation> relations = relationRepository.findAllQuery();
        List<Triplet> existingTriplets = tripletRepository.findAllQuery();

        List<GraphKnowledge> graphList = new ArrayList<>();
        List<List<Triplet>> allTripletGroups = new ArrayList<>(); // lưu tạm các triplet theo thứ tự

        // Bước 1: Tạo GraphKnowledge và gom triplet theo thứ tự
        for (Chapter ch : law.getChapters()) {
            for (Article a : ch.getArticles()) {
                // Article
                GraphKnowledge gkArticle = GraphKnowledge.builder().article(a).build();
                List<Triplet> articleTriplets = new ArrayList<>();
                articleTriplets.addAll(process(a.getTitle(), concepts, relations, existingTriplets));
                if (a.getContent() != null) {
                    articleTriplets.addAll(process(a.getContent(), concepts, relations, existingTriplets));
                }
                graphList.add(gkArticle);
                allTripletGroups.add(articleTriplets);

                for (Clause c : a.getClauses()) {
                    // Clause
                    GraphKnowledge gkClause = GraphKnowledge.builder().clause(c).build();
                    List<Triplet> clauseTriplets = process(c.getContent(), concepts, relations, existingTriplets);
                    graphList.add(gkClause);
                    allTripletGroups.add(clauseTriplets);

                    for (Point p : c.getPoints()) {
                        // Point
                        GraphKnowledge gkPoint = GraphKnowledge.builder().point(p).build();
                        List<Triplet> pointTriplets = process(p.getContent(), concepts, relations, existingTriplets);
                        graphList.add(gkPoint);
                        allTripletGroups.add(pointTriplets);
                    }
                }
            }
        }

        // Bước 2: Lưu toàn bộ GraphKnowledge để có ID
        List<GraphKnowledge> graphKnowledgeList = graphKnowledgeRepository.saveAll(graphList);
         // đảm bảo ID được sinh

        // Bước 3: Tạo các TripletGraph với GraphKnowledge đã có ID
        List<TripletGraph> tripletGraphsToSave = new ArrayList<>();

        Set<String> existingKeys = new HashSet<>();

        for (int i = 0; i < graphKnowledgeList.size(); i++) {
            GraphKnowledge gk = graphKnowledgeList.get(i);
            List<Triplet> triplets = allTripletGroups.get(i);

            for (int j = 0; j < triplets.size(); j++) {
                Triplet triplet = triplets.get(j);
                String key = gk.getId() + "-" + triplet.getId(); // unique key for composite ID
                System.out.println(key);
                if (!existingKeys.contains(key)) {
                    TripletGraph tg = new TripletGraph();
                    tg.setTriplet(triplet);
                    tg.setGraphKnowledge(gk);
                    tg.setIsRoot(j == 0);
                    tripletGraphsToSave.add(tg);
                    existingKeys.add(key);
                }
            }
        }

        // Bước 4: Lưu toàn bộ TripletGraph
        tripletGraphRepository.saveAll(tripletGraphsToSave);
    }


    public List<Triplet> process(String sentences, List<Concept> concepts, List<Relation> relations, List<Triplet> triplets) {

//        List<String> sentencePretreatment = aiService.pretreatmentDoc(sentences);


        List<Sentence> sentenceList = vnCoreNlpService.analyzeText(sentences);

        return sentenceList.stream().map(sentence -> {
            try {
//                System.out.println("cau :" + sentence);
                return processMonoSentence(sentence, concepts, relations, triplets);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).flatMap(List::stream).toList();
    }

    public Concept addConceptsToListAndDatabase(String name, List<Concept> conceptList) {
        String wordForm = name.replace('_', ' ');
        Optional<Concept> existing = conceptRepository.findByName(wordForm);
        if (existing.isPresent()) {
            return existing.get();
        }
        // Tạo mới Concept
        Concept newConcept = new Concept();
        newConcept.setName(wordForm);
        Set<String> similarWords = word2VecService.findSimilarWords(name, 5).stream()
                .map(word -> word.replace("_", " "))
                .collect(Collectors.toSet());
        newConcept.setSimilar(similarWords);
        // Lưu vào cơ sở dữ liệu
        Concept saved = conceptRepository.save(newConcept);
        // Thêm vào danh sách hiện tại
        conceptList.add(saved);
        return saved;
    }

    public Relation addRelationToListAndDatabase(String name, List<Relation> relationList) {
        String wordForm = name.replace('_', ' ');
        // Tạo mới Concept
        Relation newRelation = new Relation();
        newRelation.setName(wordForm);
        Set<String> similarWords = new HashSet<>(
                word2VecService.findSimilarWords(name, 5).stream()
                        .map(word -> word.replace("_", " ")) // thay _ bằng dấu cách
                        .collect(Collectors.toList())
        );
        newRelation.setSimilar(similarWords);
        // Lưu vào cơ sở dữ liệu
        Relation saved = relationRepository.save(newRelation);
        // Thêm vào danh sách hiện tại
        relationList.add(saved);
        return saved;
    }


    private List<Triplet> processMonoSentence(Sentence sentence,  List<Concept> concepts, List<Relation> relations, List<Triplet> triplets) throws IOException {
        List<Word> words = sentence.getWords().stream().toList();
        Multimap<Integer, List<Word>> subjectMap = utilService.deleteDuplicateWord(ruleParser.executeRules(words, "subject_rule.drl"));
        Multimap<Integer, List<Word>> relationMap = utilService.deleteDuplicateWord(ruleParser.executeRules(words, "relation_rule.drl"));

//        System.out.println("Concept List:");
//        for (Map.Entry<Integer, List<Word>> entry : subjectMap.entries()) {
//            Integer key = entry.getKey();
//            List<Word> value = entry.getValue();
//            System.out.println("Key: " + key + ", Value: " + value.stream()
//                    .map(Word::getForm)
//                    .collect(Collectors.joining(" ")));
//        }
//
//        System.out.println("Relation List:");
//        for (Map.Entry<Integer, List<Word>> entry : relationMap.entries()) {
//            Integer key = entry.getKey();
//            List<Word> value = entry.getValue();
//            System.out.println("Key: " + key + ", Value: " + value.stream()
//                    .map(Word::getForm)
//                    .collect(Collectors.joining(" ")));
//
//        }

        System.out.println(sentence);

        SentenceType sentenceType = ruleParser.executeSentenceTypeRules(words);

        Multimap<Integer, Concept> conceptList = getConcept(concepts, subjectMap);
        Multimap<Integer, Relation> relationList = getRelation(relations, relationMap);

        List<Triplet> tripletList = getTriplet(conceptList, relationList, triplets, sentenceType);

        Multimap<Integer, List<Word>> subjectRoot = ArrayListMultimap.create();
        Multimap<Integer, List<Word>> relationRootMap = ArrayListMultimap.create();

        for (Word word : words) {
            if ("V".equals(word.getPosTag()) && "root".equals(word.getDepLabel())) {
                int rootIndex = word.getIndex();
                relationRootMap.put(rootIndex, List.of(word));
                // Lấy các từ đứng trước
                List<Word> beforeRoot = words.stream()
                        .filter(w -> w.getIndex() < rootIndex)
                        .collect(Collectors.toList());

                List<Word> afterRoot = words.stream()
                        .filter(w -> w.getIndex() > rootIndex)
                        .toList();

                // Gán vào multimap với key là index của root
                subjectRoot.put(rootIndex - 1, beforeRoot);
                subjectRoot.put(rootIndex + 1, afterRoot);
            }
        }

        Multimap<Integer, Concept> conceptRoot = getConcept(concepts, subjectRoot);
        Multimap<Integer, Relation> relationRoot = getRelation(relations, relationRootMap);

        List<Triplet> tripletListRoot = getTriplet(conceptRoot, relationRoot, triplets, sentenceType);

        tripletListRoot.addAll(tripletList);

        return tripletListRoot;
    }

    public Multimap<Integer, Concept> getConcept(List<Concept> concepts, Multimap<Integer, List<Word>> words) {
        // Sử dụng ArrayListMultimap để có thể lưu trữ nhiều concept cho mỗi wordIndex
        Multimap<Integer, Concept> result = ArrayListMultimap.create();

        // Duyệt qua từng từ trong danh sách words
        for (Map.Entry<Integer, List<Word>> entry : words.entries()) {
            boolean conceptExist = false;
            int wordIndex = entry.getKey(); // Lấy index của từ trong câu
            List<Word> wordList = entry.getValue();
            String word = String.join("_", wordList.stream().map(Word::getForm).toList());

            // Duyệt qua danh sách các concept để tìm concept tương ứng
            for (Concept concept : concepts) {
                if (utilService.matchConcept(concept, word)) {
                    conceptExist = true;
                    result.put(wordIndex, concept); // Gán concept vào Multimap
                    break; // Nếu đã tìm thấy concept khớp, không cần kiểm tra thêm
                }
            }

            // Nếu không tìm thấy concept, thêm concept mới vào Multimap
            if (!conceptExist) {
                Concept newConcept = addConceptsToListAndDatabase(word, concepts);
                result.put(wordIndex, newConcept); // Thêm concept mới vào Multimap
            }
        }

        return result;
    }

    private List<Triplet> getTriplet(Multimap<Integer, Concept> concepts, Multimap<Integer, Relation> relations, List<Triplet> tripletList, SentenceType sentenceType) {
        List<Triplet> triplets = new ArrayList<>();

        // Duyệt qua các Relation
        for (Map.Entry<Integer, Relation> entry : relations.entries()) {  // sử dụng .entries() để duyệt qua các cặp key-value
            int relationIndex = entry.getKey();
            Relation relation = entry.getValue();
            System.out.println("relation: " + relation.getName());

            List<Concept> subjects = concepts.entries().stream()
                    .filter(conceptEntry -> conceptEntry.getKey() < relationIndex)
                    .map(Map.Entry::getValue)
                    .toList();

            // Lấy tất cả objects sau relationIndex
            List<Concept> objects = concepts.entries().stream()
                    .filter(conceptEntry -> conceptEntry.getKey() > relationIndex)
                    .map(Map.Entry::getValue)
                    .toList();

            System.out.println("object ");
            objects.forEach(o -> System.out.println(o.getName()));

            // Nếu không có subject/object, thêm null để vẫn tạo Triplet thiếu
            if (subjects.isEmpty() && objects.isEmpty()) continue;
            if (subjects.isEmpty()) subjects = Collections.singletonList(null);
            if (objects.isEmpty()) objects = Collections.singletonList(null);

            for (Concept subject : subjects) {
                for (Concept object : objects) {
                    Triplet triplet = tripletList.stream()
                            .filter(t -> {
                                if (t.getSubject() == null) return t.getRelation().equals(relation) && t.getObject().equals(object);
                                if (t.getObject() == null) return t.getSubject().equals(subject) && t.getRelation().equals(relation);
                                return t.getSubject().equals(subject) && t.getRelation().equals(relation) && t.getObject().equals(object);
                            })
                            .findFirst()
                            .orElseGet(() -> {
                                Triplet newTriplet = new Triplet();
                                newTriplet.setSubject(subject);
                                newTriplet.setRelation(relation);
                                newTriplet.setObject(object);
                                newTriplet.setType(sentenceType);
                                tripletList.add(newTriplet); // nhớ add vào list nếu tạo mới
                                return newTriplet;
                            });

                    triplets.add(triplet);

                    // Cập nhật quan hệ nếu subject/object khác null
                    if (subject != null) relation.getConceptSubjects().add(subject);
                    if (object != null) relation.getConceptObjects().add(object);
                }
            }

            relationRepository.save(relation);
        }

        // Lưu tất cả Triplet vào repository


        // In ra các Triplet đã tạo
        triplets.forEach(t -> {
            if (t.getSubject() != null) System.out.println("subject " + t.getSubject().getName());
            if (t.getRelation() != null) System.out.println("Relation " + t.getRelation().getName());
            if (t.getObject() != null) System.out.println("Object " + t.getObject().getName());
        });

        return tripletRepository.saveAll(triplets);
    }

    public Multimap<Integer, Relation> getRelation(List<Relation> relations, Multimap<Integer, List<Word>> words) {
        Multimap<Integer, Relation> result = ArrayListMultimap.create();

        // Duyệt qua từng từ trong danh sách words
        for (Map.Entry<Integer, List<Word>> entry : words.entries()) {
            boolean relationExist = false;
            int wordIndex = entry.getKey(); // Lấy index của từ trong câu
            List<Word> wordList = entry.getValue();
            String word = String.join(" ",wordList.stream().map(Word::getForm).toList());
//            System.out.println(word);
            for (Relation relation : relations) {
                if (utilService.matchRelation(relation, word)) {
                    result.put(wordIndex, relation);
                    relationExist = true;// Gán concept vào map
                    break; // Nếu đã tìm thấy concept khớp, không cần kiểm tra thêm
                }
            }
            if (!relationExist) result.put(wordIndex, addRelationToListAndDatabase(word, relations));

        }

        return result;
    }
}
