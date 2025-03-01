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
@Table(name = "relations")
public class Relation {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private String id;

    private String name;

    private String meaning;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "relation_similar", joinColumns = @JoinColumn(name = "relation_id"))
    @Column(name = "similar")
    private Set<String> similar = new HashSet<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "relation_keyword", joinColumns = @JoinColumn(name = "relation_id"))
    @Column(name = "keyword")
    private Set<String> keyword = new HashSet<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "relation_prop", joinColumns = @JoinColumn(name = "relation_id"))
    @Column(name = "prop")
    private Set<String> prop = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinTable(
            name = "conkey_s",
            joinColumns = @JoinColumn(name = "relation_id", nullable = false),
            inverseJoinColumns = @JoinColumn(name = "concept_id", nullable = false)
    )
    private Set<Concept> conceptSubjects = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinTable(
            name = "conkey_o",
            joinColumns = @JoinColumn(name = "relation_id", nullable = false),
            inverseJoinColumns = @JoinColumn(name = "concept_id", nullable = false)
    )
    private Set<Concept> conceptObjects = new HashSet<>();

    @OneToMany(
            mappedBy = "relation",
            fetch = FetchType.LAZY
    )
    private List<Triplet> triplets;

}
