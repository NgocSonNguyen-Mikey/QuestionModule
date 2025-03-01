package org.example.questionmodule.api.repositories;

import org.example.questionmodule.api.entities.Concept;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConceptRepository extends JpaRepository<Concept, String> {
}
