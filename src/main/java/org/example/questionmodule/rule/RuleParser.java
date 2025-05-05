package org.example.questionmodule.rule;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import lombok.RequiredArgsConstructor;
import org.example.questionmodule.api.entities.SentenceType;
import org.example.questionmodule.config.DroolsConfig;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.springframework.stereotype.Component;
import vn.pipeline.Word;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class RuleParser {
    private final DroolsConfig kieConfig;

    public Multimap<Integer,List<Word>> executeRules(List<Word> words, String ruleName) throws IOException {
        KieContainer kieContainer = kieConfig.getKieContainer(ruleName);
        KieSession kieSession = kieContainer.newKieSession();

        Multimap<Integer,List<Word>> results = ArrayListMultimap.create();
        kieSession.setGlobal("results", results);

        for (Word word : words) {
            kieSession.insert(word);
        }

        kieSession.fireAllRules();
        kieSession.dispose();

        return results;
    }

    public SentenceType executeSentenceTypeRules(List<Word> words) throws IOException {
        KieContainer kieContainer = kieConfig.getKieContainer("sentence_type_rule.drl");
        KieSession kieSession = kieContainer.newKieSession();

        List<SentenceType> results = new ArrayList<>();
        kieSession.setGlobal("sentenceTypes", results);

        for (Word word : words) {
            kieSession.insert(word);
        }

        kieSession.fireAllRules();
        kieSession.dispose();
        System.out.println(results);
        return results.stream()
                .filter(type -> type == SentenceType.NEGATIVE || type == SentenceType.QUESTION)
                .findFirst()
                .orElse(SentenceType.AFFIRMATIVE);
    }
}
