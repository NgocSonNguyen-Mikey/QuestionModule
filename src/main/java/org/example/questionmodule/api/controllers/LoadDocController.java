package org.example.questionmodule.api.controllers;

import lombok.RequiredArgsConstructor;
import org.example.questionmodule.api.dtos.admin.*;
import org.example.questionmodule.api.entities.Article;
import org.example.questionmodule.api.entities.Clause;
import org.example.questionmodule.api.entities.Point;
import org.example.questionmodule.api.repositories.ArticleRepository;
import org.example.questionmodule.api.repositories.ClauseRepository;
import org.example.questionmodule.api.repositories.PointRepository;
import org.example.questionmodule.api.services.interfaces.LoadDocService;
import org.example.questionmodule.utils.exceptions.DataNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/load")
@RequiredArgsConstructor
public class LoadDocController {
    private final ArticleRepository articleRepository;
    private final ClauseRepository clauseRepository;
    private final PointRepository pointRepository;
    private final LoadDocService loadDocService;

    @PostMapping()
    public ResponseEntity<String> loadDoc(@RequestParam("file") MultipartFile file) throws IOException {
        loadDocService.loadDoc(file);
        return ResponseEntity.ok("Success");
    }

    @PostMapping("/document")
    public ResponseEntity<LawDto> onlyLoadDoc(@RequestParam("file") MultipartFile file) throws IOException {
        return ResponseEntity.ok(loadDocService.onlyLoadDoc(file));
    }

    @PostMapping("/triplet/article/{id}")
    public ResponseEntity<String> createTripletOfArticle(
            @PathVariable String id
    ){
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException(List.of("Article not found")));
        if(article.getHasGraph()) throw new DataNotFoundException(List.of("Article had graph"));
        loadDocService.createTripletOfArticle(id);
        return ResponseEntity.ok("success");
    }

    @PostMapping("/triplet/clause/{id}")
    public ResponseEntity<String> createTripletOfClause(
            @PathVariable String id
    ){
        Clause clause = clauseRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException(List.of("Clause not found")));
        if(clause.getHasGraph()) throw new DataNotFoundException(List.of("Clause had graph"));
        loadDocService.createTripletOfClause(id);
        return ResponseEntity.ok("success");
    }

    @PostMapping("/triplet/point/{id}")
    public ResponseEntity<String> createTripletOfPoint(
            @PathVariable String id
    ){
        Point point = pointRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException(List.of("Point not found")));
        if(point.getHasGraph()) throw new DataNotFoundException(List.of("Point had graph"));
        loadDocService.createTripletOfPoint(id);
        return ResponseEntity.ok("success");
    }
}
