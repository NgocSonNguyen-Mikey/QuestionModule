package org.example.questionmodule.api.services;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import lombok.RequiredArgsConstructor;
import org.example.questionmodule.api.entities.Concept;
import org.example.questionmodule.api.entities.Relation;
import org.example.questionmodule.api.services.interfaces.UtilService;
import org.springframework.stereotype.Service;
import vn.pipeline.Word;

import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class DefaultUtilService implements UtilService {

    public boolean matchRelation(Relation relation, String wordForm) {
        if (wordForm == null || relation == null) {
            return false;
        }
        wordForm = wordForm.replace('_', ' ');

        // So sánh với name
        if (relation.getName().equalsIgnoreCase(wordForm)) {
            return true;
        }

        // So sánh với danh sách similar
        if (relation.getSimilar() != null) {
            for (String similarWord : relation.getSimilar()) {
                if (similarWord.equalsIgnoreCase(wordForm)) {
                    return true;
                }
            }
        }

        // So sánh với danh sách keyphrases
        if (relation.getKeyword() != null) {
            for (String keyphrase : relation.getKeyword()) {
                if (keyphrase.equalsIgnoreCase(wordForm)) {
                    return true;
                }
            }
        }

        return false;
    }

    public boolean matchConcept(Concept concept, String wordForm) {
        if (wordForm == null || concept == null) {
            return false;
        }
        wordForm = wordForm.replace('_', ' ').trim();

//        System.out.println(wordForm);
//        System.out.println(concept.getName());
        if (concept.getName().equalsIgnoreCase(wordForm)) {
            return true;
        }

        // So sánh với danh sách similar
        if (concept.getSimilar() != null) {
            for (String similarWord : concept.getSimilar()) {
                if (similarWord.equalsIgnoreCase(wordForm)) {
                    return true;
                }
            }
        }

        // So sánh với danh sách keyphrases
        if (concept.getKeyphrases() != null) {
            for (String keyphrase : concept.getKeyphrases()) {
                if (keyphrase.equalsIgnoreCase(wordForm)) {
                    return true;
                }
            }
        }

        return false;
    }


    public Multimap<Integer, Concept> getConcept(List<Concept> concepts, Multimap<Integer, List<Word>> words) {
        Multimap<Integer, Concept> result = ArrayListMultimap.create();

        // Duyệt qua từng từ trong danh sách words
        for (Map.Entry<Integer, List<Word>> entry : words.entries()) {
            int wordIndex = entry.getKey(); // Lấy index của từ trong câu
            List<Word> wordList = entry.getValue();
            String word = String.join(" ",wordList.stream().map(Word::getForm).toList());
            for (Concept concept : concepts) {
//                    System.out.println(concept.getName());
                if (matchConcept(concept,word)) {
//                        System.out.println(concept.getName());
                    result.put(wordIndex, concept); // Gán concept vào map
                    break; // Nếu đã tìm thấy concept khớp, không cần kiểm tra thêm
                }
            }

        }

        return result;
    }

    // Hàm so sánh từ với concept


    public Multimap<Integer, Relation> getRelation(List<Relation> relations, Multimap<Integer, List<Word>> words) {
        Multimap<Integer, Relation> result = ArrayListMultimap.create();

        // Duyệt qua từng từ trong danh sách words
        for (Map.Entry<Integer, List<Word>> entry : words.entries()) {
            int wordIndex = entry.getKey(); // Lấy index của từ trong câu
            List<Word> wordList = entry.getValue();
            String word = String.join(" ",wordList.stream().map(Word::getForm).toList());
//            System.out.println(word);
            for (Relation relation : relations) {
                if (matchRelation(relation, word)) {
                    result.put(wordIndex, relation); // Gán concept vào map
                    break; // Nếu đã tìm thấy concept khớp, không cần kiểm tra thêm
                }
            }

        }

        return result;
    }

    public Multimap<Integer, List<Word>> deleteDuplicateWord ( Multimap<Integer, List<Word>> multimap){
        Multimap<Integer, List<Word>> uniqueMap = ArrayListMultimap.create();
        Set<String> seen = new HashSet<>();

        for (Map.Entry<Integer, List<Word>> entry : multimap.entries()) {
            Integer key = entry.getKey();
            List<Word> value = entry.getValue();

            // Tạo chuỗi định danh cho cặp key-value
            String identifier = key + ":" + value.stream().map(Word::getForm).collect(Collectors.joining("_"));

            if (!seen.contains(identifier)) {
                seen.add(identifier);
                uniqueMap.put(key, value);
            }
        }

// Cập nhật multimap ban đầu nếu cần
        multimap.clear();
        multimap.putAll(uniqueMap);
        return uniqueMap;
    }
}
