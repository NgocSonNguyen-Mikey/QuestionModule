package org.example.questionmodule.api.services.interfaces;

import org.example.questionmodule.api.dtos.admin.ArticleDto;
import org.example.questionmodule.api.dtos.admin.ClauseDto;
import org.example.questionmodule.api.dtos.admin.LawDto;
import org.example.questionmodule.api.dtos.admin.PointDto;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface LoadDocService {
    String loadDoc(MultipartFile file) throws IOException;
    LawDto onlyLoadDoc(MultipartFile file) throws IOException;
    void createTripletOfArticle(String id);
    void createTripletOfClause(String id);
    void createTripletOfPoint(String id);
}
