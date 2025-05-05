package org.example.questionmodule.api.repositories;

import org.example.questionmodule.api.entities.Concept;
import org.example.questionmodule.api.entities.Relation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RelationRepository extends JpaRepository<Relation, String> {

    @Query("SELECT r FROM Relation r" +
            " left join FETCH r.keyword"+
            " left join FETCH r.similar")
    List<Relation> findAllQuery();
}
