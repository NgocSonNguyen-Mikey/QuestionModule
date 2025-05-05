package org.example.questionmodule.api.repositories;

import org.example.questionmodule.api.entities.Law;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LawRepository extends JpaRepository<Law, String> {
}
