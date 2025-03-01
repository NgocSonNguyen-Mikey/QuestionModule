package org.example.questionmodule.api.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Setter
@Getter
@Entity
@Table(name = "triplets")
public class Triplet {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private String id;

    @ManyToOne
    @JoinColumn(
            name = "subject_id",
            nullable = false
    )
    private Concept subject;

    @ManyToOne
    @JoinColumn(
            name = "object_id",
            nullable = false
    )
    private Concept object;

    @ManyToOne
    @JoinColumn(
            name = "relation_id",
            nullable = false
    )
    private Relation relation;

    @ManyToMany(
            fetch = FetchType.LAZY,
            cascade = CascadeType.PERSIST
    )
    @JoinTable(
            name = "triplet_graph",
            joinColumns = @JoinColumn(name = "triplet_id", nullable = false),
            inverseJoinColumns = @JoinColumn(name = "graph_id", nullable = false)
    )
    private List<GraphKnowledge> graphKnowledge;
}
