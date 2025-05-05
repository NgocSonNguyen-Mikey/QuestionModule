package org.example.questionmodule.api.services;

import lombok.RequiredArgsConstructor;
import vn.pipeline.Annotation;
import org.springframework.stereotype.Service;
import vn.pipeline.Sentence;
import vn.pipeline.VnCoreNLP;

import java.util.List;

@Service
public class VNCoreNlpService {
    private final VnCoreNLP pipeline;


    public VNCoreNlpService() {
        try {
            this.pipeline = new VnCoreNLP();
        } catch (Exception e) {
            throw new RuntimeException("Error initializing VNCoreNLP pipeline", e);
        }
    }

    public List<Sentence> analyzeText(String text) {
        try {
//            System.out.println(text.length());
            System.out.println(text);
            // Create an annotation object
            Annotation annotation = new Annotation(text);
            pipeline.annotate(annotation);
//            List<String> a = annotation.getSentences()
//                    .stream()
//                    .map(Sentence::getWordSegmentedTaggedSentence)
//                    .toList();
//            System.out.println(a.get(0));

            // Extract annotated results
            StringBuilder result = new StringBuilder();
//            annotation.getSentences().forEach(sentence -> {
//                sentence.getWords().forEach(word -> {
//                    result.append(String.format("Word: %s, POS: %s, NER: %s, dep: %s, head: %s,  index: %s\n",
//                            word.getForm(), word.getPosTag(), word.getNerLabel(), word.getDepLabel(), word.getHead(), word.getIndex()));
//                    System.out.println(result);
//                });
//            });

            return  annotation.getSentences();
        } catch (Exception e) {
            throw new RuntimeException("Error analyzing text with VNCoreNLP", e);
        }
    }
}
