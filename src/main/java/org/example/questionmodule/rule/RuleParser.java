package org.example.questionmodule.rule;

import lombok.RequiredArgsConstructor;
import org.example.questionmodule.config.DroolsConfig;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.springframework.stereotype.Component;
import vn.pipeline.Word;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class RuleParser {
    private final DroolsConfig kieConfig;

    public List<String> executeRules(List<Word> words, String ruleName) throws IOException {
        KieContainer kieContainer = kieConfig.getKieContainer(ruleName);
        KieSession kieSession = kieContainer.newKieSession();

        List<String> results = new ArrayList<>();
        kieSession.setGlobal("subjects", results);

        for (Word word : words) {
            kieSession.insert(word);
        }

        kieSession.fireAllRules();
        kieSession.dispose();

        return results;
    }
}
