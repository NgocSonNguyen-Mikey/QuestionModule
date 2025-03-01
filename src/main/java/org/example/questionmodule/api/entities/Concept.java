package org.example.questionmodule.api.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Setter
@Getter
@Entity
@Table(name = "concepts")
public class Concept {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private String id;

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @Column(name = "meaning", nullable = false)
    private String meaning;

    @Column(name ="type", nullable = false)
    private String type;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "concept_attrs", joinColumns = @JoinColumn(name = "concept_id"))
    @Column(name = "attribute")
    private Set<String> attrs = new HashSet<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "concept_keyphrases", joinColumns = @JoinColumn(name = "concept_id"))
    @Column(name = "keyphrases")
    private Set<String> keyphrases = new HashSet<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "concept_similar", joinColumns = @JoinColumn(name = "concept_id"))
    @Column(name = "similar")
    private Set<String> similar = new HashSet<>();

    @ManyToMany(mappedBy = "conceptSubjects")
    private Set<Relation> relationSubjects;

    @ManyToMany(mappedBy = "conceptObjects")
    private Set<Relation> relationObjects;

    @OneToMany(
            mappedBy = "subject",
            fetch = FetchType.LAZY
    )
    private List<Triplet> subjectTriplets;

    @OneToMany(
            mappedBy = "object",
            fetch = FetchType.LAZY
    )
    private List<Triplet> objectTriplet;
}
