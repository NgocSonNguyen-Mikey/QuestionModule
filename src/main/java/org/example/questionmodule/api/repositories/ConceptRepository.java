package org.example.questionmodule.api.repositories;

import org.example.questionmodule.api.entities.Concept;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConceptRepository extends JpaRepository<Concept, String> {
    public Optional<Concept> findByName(String name);
    @Query("SELECT c FROM Concept c" +
            " left join FETCH c.keyphrases"+
            " left join FETCH c.similar")
    List<Concept> findAllQuery();
}
