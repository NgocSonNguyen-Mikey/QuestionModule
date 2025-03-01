package org.example.questionmodule.api.repositories;

import org.example.questionmodule.api.entities.Relation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RelationRepository extends JpaRepository<Relation, String> {
}
