package org.example.questionmodule.api.repositories;

import org.example.questionmodule.api.entities.GraphKnowledge;
import org.example.questionmodule.api.entities.Triplet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GraphKnowledgeRepository extends JpaRepository<GraphKnowledge, String> {
    @Query("SELECT gk FROM GraphKnowledge gk " +
            "left JOIN FETCH gk.tripletGraphs tg " +
            "join fetch tg.triplet")
    List<GraphKnowledge> findAllQuery();
}
