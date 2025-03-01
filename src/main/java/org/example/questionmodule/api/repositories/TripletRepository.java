package org.example.questionmodule.api.repositories;

import org.example.questionmodule.api.entities.Triplet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TripletRepository extends JpaRepository<Triplet, String> {
    Triplet findByRelationId(String relationId);

    List<Triplet> getAll();

    @Query("SELECT t FROM Triplet t "+
            "LEFT JOIN Concept c1 ON c1.id = t.subject.id "+
            "LEFT JOIN Relation r ON r.id = t.object.id "+
            "LEFT JOIN Concept c2 ON c2.id = t.relation.id "+
            "WHERE t.subject.id LIKE ?1 "+
            "AND t.object.id LIKE ?2 "+
            "AND t.relation.id LIKE ?3")
    List<Triplet> getGraphFromTriplet(String subject_id, String object_id, String relation_id);
}
