package org.example.questionmodule.config;

import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.embeddings.wordvectors.WordVectors;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.annotation.EnableAsync;

import java.io.File;
import java.io.IOException;

@Configuration
public class AppConfig {
    @Bean
    public ChatClient chatClient(ChatClient.Builder chatClientBuilder) {
        return chatClientBuilder.build();
    }

//    @Bean
//    public WordVectors wordVectors() throws IOException {
//        // Load file từ resources
//        ClassPathResource resource = new ClassPathResource("word2vec_model/wiki.vi.vec");
//
//        // Đảm bảo file tồn tại
//        if (!resource.exists()) {
//            throw new IOException("Không tìm thấy mô hình cc.vi.300.vec trong classpath:/model/");
//        }
//
//        File modelFile = resource.getFile();
//        System.out.println("✅ Mô hình Word2Vec (.vec) đã được tải thành công!");
//
//        return WordVectorSerializer.readWord2VecModel(modelFile, true);
//
//    }
}
