package org.example.questionmodule.api.repositories;

import org.example.questionmodule.api.entities.TripletGraph;
import org.example.questionmodule.api.entities.TripletGraphId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TripletGraphRepository extends JpaRepository<TripletGraph, TripletGraphId> {

}
