package org.example.questionmodule.api.repositories;

import org.example.questionmodule.api.entities.Clause;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClauseRepository extends JpaRepository<Clause, String> {
}
