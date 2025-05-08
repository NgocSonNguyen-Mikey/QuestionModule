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
@Table(name = "clauses")
public class Clause {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "code", nullable = false)
    private String code;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @ManyToOne
    @JoinColumn(
            name = "article_id",
            nullable = false
    )
    private Article article;

    @OneToMany(
            mappedBy = "clause",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL
    )
    private List<Point> points;

    private String replaceBy;

    @Column(name = "has_graph", columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean hasGraph = false;

    @OneToOne(mappedBy = "clause",cascade = CascadeType.ALL)
    private GraphKnowledge graphKnowledge;

}
