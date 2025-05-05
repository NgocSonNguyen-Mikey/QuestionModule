package org.example.questionmodule.api.services;

import lombok.RequiredArgsConstructor;
import org.antlr.v4.runtime.misc.Triple;
import org.example.questionmodule.api.dtos.admin.*;
import org.example.questionmodule.api.dtos.ResponseDto;
import org.example.questionmodule.api.entities.*;
import org.example.questionmodule.api.repositories.LawRepository;
import org.example.questionmodule.api.services.interfaces.LawService;
import org.example.questionmodule.api.services.mapper.*;
import org.example.questionmodule.utils.dtos.ListResponse;
import org.example.questionmodule.utils.exceptions.DataNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class DefaultLawService implements LawService {
    private final LawRepository lawRepository;
    private final LawMapper lawMapper;
    private final ChapterMapper chapterMapper;
    private final ArticleMapper articleMapper;
    private final TripletMapper tripletMapper;
    private final ClauseMapper clauseMapper;
    private final PointMapper pointMapper;

    @Override
    public ListResponse<LawDto> getAllLaw(){
        List<Law> lawList = lawRepository.findAll();
        return ListResponse.<LawDto>builder()
                .data(lawMapper.toAdminDtoList(lawList))
                .size(lawList.size())
                .build();
    }

    @Override
    public LawDto getLawById(String id){
        Law lawEntity = lawRepository.findById(id).orElseThrow(() -> new DataNotFoundException(List.of("Law not found with id: " + id)));

        return lawMapper.toAdminDto(lawEntity);

    }

    @Override
    public ChapterDto getChapterByCode(String lawId, String chapterCode){
        Law lawEntity = lawRepository.findById(lawId).orElseThrow(() -> new DataNotFoundException(List.of("Law not found with id: " + lawId)));
        Chapter chapterEntity = lawEntity.getChapters().stream()
                .filter(c -> c.getCode().equals(chapterCode))
                .findFirst()
                .orElseThrow(() -> new DataNotFoundException(List.of("Chapter not found with id: " + chapterCode)));
        return chapterMapper.toAdminDto(chapterEntity);

    }

    @Override
    public ArticleDto getArticleByCode(String lawId, String chapterCode, String articleCode){
        Law lawEntity = lawRepository.findById(lawId).orElseThrow(() -> new DataNotFoundException(List.of("Law not found with id: " + lawId)));
        Chapter chapterEntity = lawEntity.getChapters().stream()
                .filter(c -> c.getCode().equals(chapterCode))
                .findFirst()
                .orElseThrow(() -> new DataNotFoundException(List.of("Chapter not found with id: " + chapterCode)));

        Article articleEntity = chapterEntity.getArticles().stream()
                .filter(a -> a.getCode().equals(articleCode))
                .findFirst()
                .orElseThrow(() -> new DataNotFoundException(List.of("Article not found with id: " + articleCode)));

        List<Triplet> triplets = articleEntity.getGraphKnowledge().getTripletGraphs()
                .stream()
                .map(TripletGraph::getTriplet)
                .toList();

        org.example.questionmodule.api.dtos.admin.ArticleDto articleDto = articleMapper.toAdminDto(articleEntity);
        articleDto.setGraph(tripletMapper.toAdminDtoList(triplets));
        return articleDto;

    }

    @Override
    public ClauseDto getClauseByCode(String lawId, String chapterCode, String articleCode, String clauseCode){
        Law lawEntity = lawRepository.findById(lawId).orElseThrow(() -> new DataNotFoundException(List.of("Law not found with id: " + lawId)));
        Chapter chapterEntity = lawEntity.getChapters().stream()
                .filter(c -> c.getCode().equals(chapterCode))
                .findFirst()
                .orElseThrow(() -> new DataNotFoundException(List.of("Chapter not found with id: " + chapterCode)));

        Article articleEntity = chapterEntity.getArticles().stream()
                .filter(a -> a.getCode().equals(articleCode))
                .findFirst()
                .orElseThrow(() -> new DataNotFoundException(List.of("Article not found with id: " + articleCode)));

        Clause clauseEntity = articleEntity.getClauses().stream()
                .filter(cl -> cl.getCode().equals(clauseCode))
                .findFirst()
                .orElseThrow(() -> new DataNotFoundException(List.of("Clause not found with id: " + clauseCode)));

        List<Triplet> triplets = articleEntity.getGraphKnowledge().getTripletGraphs()
                .stream()
                .map(TripletGraph::getTriplet)
                .toList();

        ClauseDto clauseDto = clauseMapper.toAdminDto(clauseEntity);
        clauseDto.setGraph(tripletMapper.toAdminDtoList(triplets));

        return clauseDto;

    }

    @Override
    public PointDto getPointByCode(String lawId, String chapterCode, String articleCode, String clauseCode, String pointCode) {
        Law lawEntity = lawRepository.findById(lawId).orElseThrow(() -> new DataNotFoundException(List.of("Law not found with id: " + lawId)));
        Chapter chapterEntity = lawEntity.getChapters().stream()
                .filter(c -> c.getCode().equals(chapterCode))
                .findFirst()
                .orElseThrow(() -> new DataNotFoundException(List.of("Chapter not found with id: " + chapterCode)));

        Article articleEntity = chapterEntity.getArticles().stream()
                .filter(a -> a.getCode().equals(articleCode))
                .findFirst()
                .orElseThrow(() -> new DataNotFoundException(List.of("Article not found with id: " + articleCode)));

        Clause clauseEntity = articleEntity.getClauses().stream()
                .filter(cl -> cl.getCode().equals(clauseCode))
                .findFirst()
                .orElseThrow(() -> new DataNotFoundException(List.of("Clause not found with id: " + clauseCode)));

        Point pointEntity = clauseEntity.getPoints().stream()
                .filter(p -> p.getCode().equals(pointCode))
                .findFirst()
                .orElseThrow(() -> new DataNotFoundException(List.of("Point not found with id: " + pointCode)));

        List<Triplet> triplets = articleEntity.getGraphKnowledge().getTripletGraphs()
                .stream()
                .map(TripletGraph::getTriplet)
                .toList();

        PointDto pointDto = pointMapper.toAdminDto(pointEntity);
        pointDto.setGraph(tripletMapper.toAdminDtoList(triplets));

        return pointDto;
    }

}
