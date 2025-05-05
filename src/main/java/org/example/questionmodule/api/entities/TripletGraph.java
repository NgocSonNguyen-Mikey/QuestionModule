package org.example.questionmodule.api.entities;

import jakarta.persistence.*;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Setter
@Getter
@Entity
@Table(name = "triplet_graph")
public class TripletGraph {
    @EmbeddedId
    private TripletGraphId id = new TripletGraphId(); ;

    @ManyToOne
    @MapsId("tripletId")
    @JoinColumn(name = "triplet_id")
    private Triplet triplet;

    @ManyToOne
    @MapsId("graphId")
    @JoinColumn(name = "graph_id")
    private GraphKnowledge graphKnowledge;

    @Column(name = "is_root")
    private Boolean isRoot;
}
