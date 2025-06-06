package org.example.questionmodule.api.repositories;

import org.example.questionmodule.api.entities.Chapter;
import org.example.questionmodule.api.entities.Relation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChapterRepository extends JpaRepository<Chapter, String> {
    @Query("SELECT c FROM Chapter c " +
            "join fetch c.articles a " +
            "join fetch a.clauses l " +
            "join fetch l.points p " +
            "WHERE c.id = :chapterId")
    Optional<Chapter> findAllQuery(@Param("chapterId") String chapterId);
}
