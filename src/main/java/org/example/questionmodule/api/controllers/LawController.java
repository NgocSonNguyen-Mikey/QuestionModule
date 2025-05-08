package org.example.questionmodule.api.controllers;

import lombok.RequiredArgsConstructor;
import org.example.questionmodule.api.dtos.admin.*;
import org.example.questionmodule.api.services.interfaces.LawService;
import org.example.questionmodule.utils.dtos.ListResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class LawController {
    private final LawService lawService;

    @GetMapping("/law")
    public ListResponse<LawDto> getAll(){
        return lawService.getAllLaw();
    }

    @GetMapping("/law/{id}")
    public ResponseEntity<LawDto> getLawById(@PathVariable("id") String id){
        return ResponseEntity.ok(lawService.getLawById(id));
    }

    @GetMapping("/law/{id}/{chapterCode}")
    public ResponseEntity<ChapterDto> getChapterByCode(@PathVariable("id") String id,
                                       @PathVariable("chapterCode") String chapterCode
    ){
        return ResponseEntity.ok(lawService.getChapterByCode(id, chapterCode));
    }

    @GetMapping("/law/{id}/{chapterCode}/{articleCode}")
    public ResponseEntity<ArticleDto> getArticleByCode(@PathVariable("id") String id,
                                       @PathVariable("chapterCode") String chapterCode,
                                       @PathVariable("articleCode") String articleCode
    ){
        return ResponseEntity.ok(lawService.getArticleByCode(id, chapterCode, articleCode));
    }

    @GetMapping("/law/{id}/{chapterCode}/{articleCode}/{clauseCode}")
    public ResponseEntity<ClauseDto> getClauseByCode(@PathVariable("id") String id,
                                                    @PathVariable("chapterCode") String chapterCode,
                                                    @PathVariable("articleCode") String articleCode,
                                                    @PathVariable("clauseCode") String clauseCode
    ){
        return ResponseEntity.ok(lawService.getClauseByCode(id, chapterCode, articleCode, clauseCode));
    }

    @GetMapping("/law/{id}/{chapterCode}/{articleCode}/{clauseCode}/{pointCode}")
    public ResponseEntity<PointDto> getPointByCode(@PathVariable("id") String id,
                                   @PathVariable("chapterCode") String chapterCode,
                                   @PathVariable("articleCode") String articleCode,
                                   @PathVariable("clauseCode") String clauseCode,
                                   @PathVariable("pointCode") String pointCode
    ){
        return ResponseEntity.ok(lawService.getPointByCode(id, chapterCode, articleCode, clauseCode, pointCode));
    }

    @DeleteMapping("/article/{id}")
    public ResponseEntity<ArticleDto> deleteTripletOfArticle(
            @PathVariable String id,
            @RequestBody TripletDelete triplet
    ){
        return ResponseEntity.ok(lawService.deleteTripletOfArticle(id, triplet.getTripletId()));
    }

    @DeleteMapping("/clause/{id}")
    public ResponseEntity<ClauseDto> deleteTripletOfClause(
            @PathVariable String id,
            @RequestBody TripletDelete triplet
    ){
        System.out.println(triplet.getTripletId());
        return ResponseEntity.ok(lawService.deleteTripletOfClause(id, triplet.getTripletId()));
    }

    @DeleteMapping("/point/{id}")
    public ResponseEntity<PointDto> deleteTripletOfPoint(
            @PathVariable String id,
            @RequestBody TripletDelete triplet
    ){
        return ResponseEntity.ok(lawService.deleteTripletOfPoint(id, triplet.getTripletId()));
    }

    @PostMapping("/article/{id}")
    public ResponseEntity<ArticleDto> addTripletOfArticle(
            @PathVariable String id,
            @RequestBody TripletRequest triplet
    ){
        return ResponseEntity.ok(lawService.addTripletToArticle(id, triplet));
    }

    @PostMapping("/clause/{id}")
    public ResponseEntity<ClauseDto> addTripletOfClause(
            @PathVariable String id,
            @RequestBody TripletRequest triplet
    ){
        return ResponseEntity.ok(lawService.addTripletToClause(id, triplet));
    }

    @PostMapping("/point/{id}")
    public ResponseEntity<PointDto> addTripletOfPoint(
            @PathVariable String id,
            @RequestBody TripletRequest triplet
    ){
        return ResponseEntity.ok(lawService.addTripletToPoint(id, triplet));
    }
}
