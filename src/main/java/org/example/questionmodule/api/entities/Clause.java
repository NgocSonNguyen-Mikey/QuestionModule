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

    @Column(name = "content", nullable = false)
    private String content;

    @ManyToOne
    @JoinColumn(
            name = "article_id",
            nullable = false
    )
    private Article article;

    @OneToMany(
            mappedBy = "clause",
            fetch = FetchType.LAZY
    )
    private List<Point> points;

    private String replaceBy;

    @OneToOne(mappedBy = "clause")
    private GraphKnowledge graphKnowledge;

}
