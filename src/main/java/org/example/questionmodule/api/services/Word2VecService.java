package org.example.questionmodule.api.services;

import jakarta.annotation.PostConstruct;
import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.embeddings.wordvectors.WordVectors;
import org.deeplearning4j.models.word2vec.Word2Vec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.Collection;

@Service
public class Word2VecService {

    private Word2Vec wordVectors;
    private volatile boolean loading = false;

    @Value("${word2vec.model.path}")
    private String modelPath;

    @PostConstruct
    public void init() {
        // Nạp mô hình trong thread riêng khi service khởi tạo
        new Thread(this::loadModel).start();
//        wordVectors = new Word2Vec();
    }

    private void loadModel() {
        try {
            loading = true;// Đổi lại đúng đường dẫn trên máy bạn
            System.out.println(modelPath);
            File modelFile = new File(modelPath);

            if (!modelFile.exists()) {
                throw new IOException("Model file not found: " + modelFile.getAbsolutePath());
            }
                this.wordVectors = WordVectorSerializer.readWord2VecModel(modelFile, true); // <- Đúng cho file .vec
            System.out.println("✅ Word2Vec model loaded successfully.");

//                WordVectorSerializer.writeWord2VecModel((Word2Vec) this.wordVectors, binOutput);
//                System.out.println("✅ File binary đã được ghi tại: " + binOutput.getAbsolutePath());
        } catch (IOException e) {
            System.err.println("❌ Failed to load Word2Vec model: " + e.getMessage());
        } finally {
            loading = false;
        }
    }


    public Collection<String> findSimilarWords(String word, int topN) {
        if (wordVectors == null) {
            throw new IllegalStateException("Mô hình chưa được load!");
        }
        return wordVectors.wordsNearest(word, topN);
    }

    public double[] getWordVector(String word) {
        if (wordVectors == null) {
            throw new IllegalStateException("Mô hình chưa được load!");
        }
        return wordVectors.getWordVector(word);
    }

}
