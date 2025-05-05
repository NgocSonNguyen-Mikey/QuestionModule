package org.example.questionmodule.api.controllers;

import lombok.RequiredArgsConstructor;
import org.example.questionmodule.api.dtos.admin.*;
import org.example.questionmodule.api.services.interfaces.LawService;
import org.example.questionmodule.utils.dtos.ListResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/law")
@RequiredArgsConstructor
public class LawController {
    private final LawService lawService;

    @GetMapping("")
    public ListResponse<LawDto> getAll(){
        return lawService.getAllLaw();
    }

    @GetMapping("/{id}")
    public LawDto getLawById(@PathVariable("id") String id){
        return lawService.getLawById(id);
    }

    @GetMapping("/{id}/{chapterCode}")
    public ChapterDto getChapterByCode(@PathVariable("id") String id,
                                       @PathVariable("chapterCode") String chapterCode
    ){
        return lawService.getChapterByCode(id, chapterCode);
    }

    @GetMapping("/{id}/{chapterCode}/{articleCode}")
    public ArticleDto getArticleByCode(@PathVariable("id") String id,
                                       @PathVariable("chapterCode") String chapterCode,
                                       @PathVariable("articleCode") String articleCode
    ){
        return lawService.getArticleByCode(id, chapterCode, articleCode);
    }

    @GetMapping("/{id}/{chapterCode}/{articleCode}/{clauseCode}")
    public ResponseEntity<ClauseDto> getClauseByCode(@PathVariable("id") String id,
                                                    @PathVariable("chapterCode") String chapterCode,
                                                    @PathVariable("articleCode") String articleCode,
                                                    @PathVariable("clauseCode") String clauseCode
    ){
        return ResponseEntity.ok(lawService.getClauseByCode(id, chapterCode, articleCode, clauseCode));
    }

    @GetMapping("/{id}/{chapterCode}/{articleCode}/{clauseCode}/{pointCode}")
    public PointDto getPointByCode(@PathVariable("id") String id,
                                   @PathVariable("chapterCode") String chapterCode,
                                   @PathVariable("articleCode") String articleCode,
                                   @PathVariable("clauseCode") String clauseCode,
                                   @PathVariable("pointCode") String pointCode
    ){
        return lawService.getPointByCode(id, chapterCode, articleCode, clauseCode, pointCode);
    }
}
