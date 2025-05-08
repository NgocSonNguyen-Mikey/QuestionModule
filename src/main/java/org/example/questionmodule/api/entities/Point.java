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
@Table(name = "points")
public class Point {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "code", nullable = false)
    private String code;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @ManyToOne
    @JoinColumn(
            name = "clause_id",
            nullable = false
    )
    private Clause clause;

    private String replaceBy;

    @Column(name = "has_graph", columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean hasGraph = false;

    @OneToOne(mappedBy = "point", cascade = CascadeType.ALL)
    private GraphKnowledge graphKnowledge;
}
