package org.example.questionmodule.api.entities;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Type;

import java.util.List;

@AllArgsConstructor
@Builder
@Setter
@Getter
@Entity
@Table(name = "articles")
@NoArgsConstructor
public class Article {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "code", nullable = false)
    private String code;

    @Column(name = "content", nullable = false)
    private String content;

    @ManyToOne
    @JoinColumn(
            name = "chapter_id",
            nullable = false
    )
    private Chapter chapter;

    @OneToMany(
            mappedBy = "article",
            fetch = FetchType.LAZY
    )
    private List<Clause> clauses;

    private String replaceBy;

    @OneToOne(mappedBy = "article")
    private GraphKnowledge graphKnowledge;
}
