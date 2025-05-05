package org.example.questionmodule.api.services;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.rag.Query;
import org.springframework.ai.rag.preretrieval.query.transformation.QueryTransformer;
import org.springframework.ai.rag.preretrieval.query.transformation.RewriteQueryTransformer;
import org.springframework.ai.rag.retrieval.search.DocumentRetriever;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.text.BreakIterator;
import java.util.*;

@RequiredArgsConstructor
@Service
public class AIService {

    private final ChatClient chatClient;

    @Value("classpath:/prompts/pretreatment.st")
    private Resource sbPromptTemplate;

    @Value("classpath:/prompts/pretreatment_document.st")
    private Resource docPromptTemplate;


    @Value("classpath:/prompts/generate_answer.st")
    private Resource generatePromptTemplate;

    public List<String> pretreatment(Resource prompt, String message) {
        Query query = new Query(message);

        QueryTransformer queryTransformer = RewriteQueryTransformer.builder()
                .promptTemplate(new PromptTemplate(prompt))
                .chatClientBuilder(chatClient.mutate())
                .targetSearchSystem("luật giao dịch điện tử")
                .build();
        String result = queryTransformer.transform(query).text();
        List<String> sentences = Arrays.stream(result.split("\\."))
                .map(String::trim) // Xóa khoảng trắng thừa
                .filter(s -> !s.isEmpty()) // Loại bỏ chuỗi rỗng nếu có
                .toList();
        return sentences;
    }

    public List<String> pretreatmentAnswer(String message) {
        return pretreatment(sbPromptTemplate, message);
    }

    public List<String> pretreatmentDoc(String message) {
        return pretreatment(docPromptTemplate, message);
    }

    public String getAnswer(String query, String doc){
        PromptTemplate promptTemplate = new PromptTemplate(generatePromptTemplate);
        Map<String, Object> promptParameters = new HashMap<>();
        promptParameters.put("query", query);
        promptParameters.put("document", doc);
        Prompt prompt = promptTemplate.create(promptParameters);
        String answer = chatClient.prompt(prompt)
                .call()
                .content();
        System.out.println("answer" + answer);
        return answer;
    }

}
