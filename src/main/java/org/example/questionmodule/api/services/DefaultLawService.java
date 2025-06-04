package org.example.questionmodule.api.services;

import lombok.RequiredArgsConstructor;
import org.antlr.v4.runtime.misc.Triple;
import org.example.questionmodule.api.dtos.admin.*;
import org.example.questionmodule.api.dtos.ResponseDto;
import org.example.questionmodule.api.entities.*;
import org.example.questionmodule.api.repositories.*;
import org.example.questionmodule.api.services.interfaces.LawService;
import org.example.questionmodule.api.services.mapper.*;
import org.example.questionmodule.utils.dtos.ListResponse;
import org.example.questionmodule.utils.exceptions.DataNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class DefaultLawService implements LawService {
    private final LawRepository lawRepository;
    private final ChapterRepository chapterRepository;
    private final ArticleRepository articleRepository;
    private final ClauseRepository clauseRepository;
    private final PointRepository pointRepository;
    private final ConceptRepository conceptRepository;
    private final RelationRepository relationRepository;
    private final TripletRepository tripletRepository;
    private final LawMapper lawMapper;
    private final ChapterMapper chapterMapper;
    private final ArticleMapper articleMapper;
    private final TripletMapper tripletMapper;
    private final ClauseMapper clauseMapper;
    private final PointMapper pointMapper;
    private final TripletGraphRepository tripletGraphRepository;

    public ListResponse<LawDto> getLawsWithTripletsAttached() {
        List<Law> laws = lawRepository.findAll();

        // Sắp xếp từng law nếu cần
        List<Law> sortedLaws = sort(laws);

        // Ánh xạ sang DTO, đồng thời gắn triplet vào từng phần tử nếu có
        List<LawDto> lawDtos = sortedLaws.stream()
                .map(law -> {
                    LawDto lawDto = lawMapper.toAdminDto(law);

                    lawDto.getChapters().forEach(chapterDto ->
                            chapterDto.getArticles().forEach(articleDto -> {

                                // Triplet của Article
                                Article articleEntity = findArticleEntity(law, chapterDto.getCode(), articleDto.getCode());
                                if (articleEntity.getGraphKnowledge() != null) {
                                    List<Triplet> triplets = extractTriplets(articleEntity.getGraphKnowledge());
                                    articleDto.setGraph(tripletMapper.toAdminDtoList(triplets));
                                }

                                // Triplet của Clause
                                articleDto.getClauses().forEach(clauseDto -> {
                                    Clause clauseEntity = findClauseEntity(articleEntity, clauseDto.getCode());
                                    if (clauseEntity != null && clauseEntity.getGraphKnowledge() != null) {
                                        List<Triplet> triplets = extractTriplets(clauseEntity.getGraphKnowledge());
                                        clauseDto.setGraph(tripletMapper.toAdminDtoList(triplets));
                                    }

                                    // Triplet của Point
                                    clauseDto.getPoints().forEach(pointDto -> {
                                        Point pointEntity = findPointEntity(clauseEntity, pointDto.getCode());
                                        if (pointEntity != null && pointEntity.getGraphKnowledge() != null) {
                                            List<Triplet> triplets = extractTriplets(pointEntity.getGraphKnowledge());
                                            pointDto.setGraph(tripletMapper.toAdminDtoList(triplets));
                                        }
                                    });
                                });

                            })
                    );

                    return lawDto;
                })
                .collect(Collectors.toList());

        return ListResponse.<LawDto>builder()
                .data(lawDtos)
                .size(lawDtos.size())
                .build();
    }


    @Override
    public ListResponse<LawDto> getAllLaw(){
        List<Law> lawList = lawRepository.findAll();

        lawList = sort(lawList);

        return ListResponse.<LawDto>builder()
                .data(lawMapper.toAdminDtoList(lawList))
                .size(lawList.size())
                .build();
    }

    private List<Law> sort(List<Law> lawList){
        lawList.forEach(law -> {
            law.getChapters().sort(Comparator.comparing(Chapter::getCode));

            law.getChapters().forEach(chapter -> {
                chapter.getArticles().sort(Comparator.comparing(Article::getCode));

                chapter.getArticles().forEach(article -> {
                    article.getClauses().sort(Comparator.comparing(Clause::getCode));

                    article.getClauses().forEach(clause -> {
                        clause.getPoints().sort(Comparator.comparing(Point::getCode));
                    });
                });
            });
        });

        return lawList;
    }

    @Override
    public LawDto getLawById(String id){
        Law lawEntity = lawRepository.findById(id).orElseThrow(() -> new DataNotFoundException(List.of("Law not found with id: " + id)));
        lawEntity = sort(List.of(lawEntity)).stream().findFirst().orElseThrow();
        return lawMapper.toAdminDto(lawEntity);

    }

    @Override
    public ChapterDto getChapterByCode(String lawId, String chapterCode){
        Law lawEntity = lawRepository.findById(lawId).orElseThrow(() -> new DataNotFoundException(List.of("Law not found with id: " + lawId)));
        lawEntity = sort(List.of(lawEntity)).stream().findFirst().orElseThrow();

        Chapter chapterEntity = lawEntity.getChapters().stream()
                .filter(c -> c.getCode().equals(chapterCode))
                .findFirst()
                .orElseThrow(() -> new DataNotFoundException(List.of("Chapter not found with id: " + chapterCode)));
        return chapterMapper.toAdminDto(chapterEntity);

    }

    @Override
    public ArticleDto getArticleByCode(String lawId, String chapterCode, String articleCode){
        Law lawEntity = lawRepository.findById(lawId).orElseThrow(() -> new DataNotFoundException(List.of("Law not found with id: " + lawId)));
        lawEntity = sort(List.of(lawEntity)).stream().findFirst().orElseThrow();

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
        lawEntity = sort(List.of(lawEntity)).stream().findFirst().orElseThrow();

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

        List<Triplet> triplets = clauseEntity.getGraphKnowledge().getTripletGraphs()
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
        lawEntity = sort(List.of(lawEntity)).stream().findFirst().orElseThrow();

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

        List<Triplet> triplets = pointEntity.getGraphKnowledge().getTripletGraphs()
                .stream()
                .map(TripletGraph::getTriplet)
                .toList();

        PointDto pointDto = pointMapper.toAdminDto(pointEntity);
        pointDto.setGraph(tripletMapper.toAdminDtoList(triplets));

        return pointDto;
    }

    @Override
    public ArticleDto deleteTripletOfArticle(String articleId, String tripletId) {
        // 1. Lấy Article
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new DataNotFoundException(List.of("Article not found")));

        // 2. Lấy GraphKnowledge từ Article
        GraphKnowledge graph = article.getGraphKnowledge();
        if (graph == null) {
            throw new DataNotFoundException(List.of("Article has no associated GraphKnowledge"));
        }

        TripletGraph tripletGraph = removeTripletFromGraph(graph, tripletId);
        // Đồng bộ lại danh sách trong graph
        graph.getTripletGraphs().remove(tripletGraph);


        List<Triplet> updatedTriplets = article.getGraphKnowledge().getTripletGraphs()
                .stream()
                .map(TripletGraph::getTriplet)
                .toList();

        org.example.questionmodule.api.dtos.admin.ArticleDto articleDto = articleMapper.toAdminDto(article);
        articleDto.setGraph(tripletMapper.toAdminDtoList(updatedTriplets));
        // 4. Trả về ArticleDto (giả sử bạn có hàm chuyển đổi)
        return articleDto; // hoặc viết manual nếu không dùng mapper
    }

    @Transactional
    @Override
    public ClauseDto deleteTripletOfClause(String clauseId, String tripletId) {
        // 1. Lấy Article
        Clause clause = clauseRepository.findById(clauseId)
                .orElseThrow(() -> new DataNotFoundException(List.of("Clause not found")));

        // 2. Lấy GraphKnowledge từ Article
        GraphKnowledge graph = clause.getGraphKnowledge();
        if (graph == null) {
            throw new DataNotFoundException(List.of("Article has no associated GraphKnowledge"));
        }

        TripletGraph tripletGraph = removeTripletFromGraph(graph, tripletId);
        // Đồng bộ lại danh sách trong graph
        graph.getTripletGraphs().remove(tripletGraph);

        // 4. Reload lại Clause nếu cần, hoặc cập nhật DTO từ object hiện tại
        ClauseDto clauseDto = clauseMapper.toAdminDto(clause);
        List<Triplet> updatedTriplets = clause.getGraphKnowledge().getTripletGraphs()
                .stream()
                .map(TripletGraph::getTriplet)
                .toList();

        clauseDto.setGraph(tripletMapper.toAdminDtoList(updatedTriplets));

        return clauseDto; // hoặc viết manual nếu không dùng mapper
    }

    @Override
    public PointDto deleteTripletOfPoint(String pointId, String tripletId) {
        Point point = pointRepository.findById(pointId)
                .orElseThrow(() -> new DataNotFoundException(List.of("Point not found")));

        GraphKnowledge graph = point.getGraphKnowledge();
        if (graph == null) {
            throw new DataNotFoundException(List.of("Article has no associated GraphKnowledge"));
        }

        TripletGraph tripletGraph = removeTripletFromGraph(graph, tripletId);
        // Đồng bộ lại danh sách trong graph
        graph.getTripletGraphs().remove(tripletGraph);


        List<Triplet> updatedTriplets = point.getGraphKnowledge().getTripletGraphs()
                .stream()
                .map(TripletGraph::getTriplet)
                .toList();

        PointDto pointDto = pointMapper.toAdminDto(point);
        pointDto.setGraph(tripletMapper.toAdminDtoList(updatedTriplets));
        return pointDto; // hoặc viết manual nếu không dùng mapper
    }

    @Override
    public ClauseDto addTripletToClause(String clauseId, TripletRequest tripletRequest){
        Clause clause = clauseRepository.findById(clauseId)
                .orElseThrow(() -> new DataNotFoundException(List.of("Clause not found")));

        GraphKnowledge graphKnowledge = clause.getGraphKnowledge();
        TripletGraph tripletGraph = createTriplet(tripletRequest, graphKnowledge);

        List<Triplet> triplets = new java.util.ArrayList<>(clause.getGraphKnowledge().getTripletGraphs()
                .stream()
                .map(TripletGraph::getTriplet)
                .toList());

        triplets.add(tripletGraph.getTriplet());

        ClauseDto clauseDto = clauseMapper.toAdminDto(clause);
        clauseDto.setGraph(tripletMapper.toAdminDtoList(triplets));
        // 4. Trả về ArticleDto (giả sử bạn có hàm chuyển đổi)
        return clauseDto; // hoặc viết manual nếu không dùng mapper
    }

    @Override
    public PointDto addTripletToPoint(String pointId, TripletRequest tripletRequest){
        Point point = pointRepository.findById(pointId)
                .orElseThrow(() -> new DataNotFoundException(List.of("Point not found")));

        GraphKnowledge graphKnowledge = point.getGraphKnowledge();
        TripletGraph tripletGraph = createTriplet(tripletRequest, graphKnowledge);

        List<Triplet> triplets = new java.util.ArrayList<>(point.getGraphKnowledge().getTripletGraphs()
                .stream()
                .map(TripletGraph::getTriplet)
                .toList());

        triplets.add(tripletGraph.getTriplet());

        PointDto pointDto = pointMapper.toAdminDto(point);
        pointDto.setGraph(tripletMapper.toAdminDtoList(triplets));
        return pointDto; // hoặc viết manual nếu không dùng mapp// hoặc viết manual nếu không dùng mapper
    }

    @Override
    public ArticleDto addTripletToArticle(String articleId, TripletRequest tripletRequest){
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new DataNotFoundException(List.of("Article not found")));

        GraphKnowledge graphKnowledge = article.getGraphKnowledge();

        TripletGraph tripletGraph = createTriplet(tripletRequest, graphKnowledge);

        List<Triplet> triplets = new java.util.ArrayList<>(article.getGraphKnowledge().getTripletGraphs()
                .stream()
                .map(TripletGraph::getTriplet)
                .toList());

        triplets.add(tripletGraph.getTriplet());

        org.example.questionmodule.api.dtos.admin.ArticleDto articleDto = articleMapper.toAdminDto(article);
        articleDto.setGraph(tripletMapper.toAdminDtoList(triplets));

        // 4. Trả về ArticleDto (giả sử bạn có hàm chuyển đổi)
        return articleDto; // hoặc viết manual nếu không dùng mapper hoặc viết manual nếu không dùng mapper
    }

    private TripletGraph removeTripletFromGraph(GraphKnowledge graph, String tripletId) {

//        graph.getTripletGraphs().forEach(t-> System.out.println(t.getTriplet().getId()));

        TripletGraph tripletGraph = graph.getTripletGraphs().stream()
                .filter(t -> t.getTriplet().getId().equals(tripletId))
                .findFirst()
                .orElseThrow(() -> new DataNotFoundException(List.of("TripletGraph not found")));

        System.out.println("Xoa:" + tripletGraph.getTriplet().getId());
        tripletGraphRepository.delete(tripletGraph);

        return tripletGraph;
    }

    private TripletGraph createTriplet(TripletRequest tripletRequest, GraphKnowledge graphKnowledge) {
        Concept subject = conceptRepository.findById(tripletRequest.getSubjectId())
                .orElseThrow(() -> new DataNotFoundException(List.of("concept not found")));
        Concept object = conceptRepository.findById(tripletRequest.getObjectId())
                .orElseThrow(() -> new DataNotFoundException(List.of("concept not found")));
        Relation relation = relationRepository.findById(tripletRequest.getRelationId())
                .orElseThrow(() -> new DataNotFoundException(List.of("Relation not found")));

        var triplet = tripletRepository.getTriplet(tripletRequest.getSubjectId(), tripletRequest.getObjectId(), tripletRequest.getRelationId())
                .orElseGet(() -> tripletRepository.save(Triplet.builder()
                        .relation(relation)
                        .object(object)
                        .subject(subject)
                        .build()));

        TripletGraphId id = new TripletGraphId(triplet.getId(), graphKnowledge.getId());

        Optional<TripletGraph> tripletGraph = tripletGraphRepository.findById(id);
        if (tripletGraph.isPresent()) throw new DataNotFoundException(List.of("Triplet already exist in this "));

        return tripletGraphRepository.save(TripletGraph.builder()
                .id(id)
                .graphKnowledge(graphKnowledge)
                .triplet(triplet)
                .isRoot(false)
                .build()
        );
    }

    private List<Triplet> extractTriplets(GraphKnowledge graph) {
        if (graph == null || graph.getTripletGraphs() == null) return List.of();
        return graph.getTripletGraphs().stream()
                .map(TripletGraph::getTriplet)
                .collect(Collectors.toList());
    }

    private Article findArticleEntity(Law law, String chapterCode, String articleCode) {
        return law.getChapters().stream()
                .filter(c -> c.getCode().equals(chapterCode))
                .flatMap(c -> c.getArticles().stream())
                .filter(a -> a.getCode().equals(articleCode))
                .findFirst()
                .orElse(null);
    }

    private Clause findClauseEntity(Article article, String clauseCode) {
        return article.getClauses().stream()
                .filter(c -> c.getCode().equals(clauseCode))
                .findFirst()
                .orElse(null);
    }

    private Point findPointEntity(Clause clause, String pointCode) {
        return clause.getPoints().stream()
                .filter(p -> p.getCode().equals(pointCode))
                .findFirst()
                .orElse(null);
    }


    public PointDto getPointById(String pointId) {
        Point point = pointRepository.findById(pointId)
                .orElseThrow(() -> new DataNotFoundException(List.of("Point not found with id: " + pointId)));

        GraphKnowledge graph = point.getGraphKnowledge();
        List<TripletDto> tripletDtos = new ArrayList<>();
        if (graph != null && graph.getTripletGraphs() != null) {
            tripletDtos = graph.getTripletGraphs().stream()
                    .map(TripletGraph::getTriplet)
                    .map(tripletMapper::toAdminDto)
                    .collect(Collectors.toList());
        }

        PointDto pointDto = pointMapper.toAdminDto(point);
        pointDto.setGraph(tripletDtos);
        return pointDto;
    }

    @Override
    public ArticleDto getArticleById(String articleId) {
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new DataNotFoundException(List.of("Article not found with id: " + articleId)));

        List<TripletDto> tripletDtos = new ArrayList<>();
        GraphKnowledge graph = article.getGraphKnowledge();
        if (graph != null && graph.getTripletGraphs() != null) {
            tripletDtos = graph.getTripletGraphs().stream()
                    .map(TripletGraph::getTriplet)
                    .map(tripletMapper::toAdminDto)
                    .collect(Collectors.toList());
        }

        ArticleDto articleDto = articleMapper.toAdminDto(article);
        articleDto.setGraph(tripletDtos);
        return articleDto;
    }

    @Override
    public ClauseDto getClauseById(String clauseId) {
        Clause clause = clauseRepository.findById(clauseId)
                .orElseThrow(() -> new DataNotFoundException(List.of("Clause not found with id: " + clauseId)));

        List<TripletDto> tripletDtos = new ArrayList<>();
        GraphKnowledge graph = clause.getGraphKnowledge();
        if (graph != null && graph.getTripletGraphs() != null) {
            tripletDtos = graph.getTripletGraphs().stream()
                    .map(TripletGraph::getTriplet)
                    .map(tripletMapper::toAdminDto)
                    .collect(Collectors.toList());
        }

        ClauseDto clauseDto = clauseMapper.toAdminDto(clause);
        clauseDto.setGraph(tripletDtos);
        return clauseDto;
    }

    @Override
    public ChapterDto getChapterById(String chapterId) {
        Chapter chapter = chapterRepository.findById(chapterId)
                .orElseThrow(() -> new DataNotFoundException(List.of("Chapter not found with id: " + chapterId)));

        return chapterMapper.toAdminDto(chapter);
    }


}
