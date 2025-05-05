package org.example.questionmodule.api.services.interfaces;

import org.example.questionmodule.api.dtos.admin.*;
import org.example.questionmodule.utils.dtos.ListResponse;

public interface LawService {
    ListResponse<LawDto> getAllLaw();
    LawDto getLawById(String id);
    ChapterDto getChapterByCode(String lawId, String chapterCode);
    public ArticleDto getArticleByCode(String lawId, String chapterCode, String articleCode);
    ClauseDto getClauseByCode(String lawId, String chapterCode, String articleCode, String clauseCode);
    PointDto getPointByCode(String lawId, String chapterCode, String articleCode, String clauseCode, String pointCode);

}
