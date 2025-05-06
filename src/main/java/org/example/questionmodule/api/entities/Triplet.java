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

    @Enumerated(EnumType.STRING)
    private SentenceType type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "subject_id",
            nullable = true
    )
    private Concept subject;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "object_id",
            nullable = true
    )
    private Concept object;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "relation_id",
            nullable = true
    )
    private Relation relation;

    @OneToMany(mappedBy = "triplet", fetch = FetchType.LAZY)
    private List<TripletGraph> tripletGraphs;
}
