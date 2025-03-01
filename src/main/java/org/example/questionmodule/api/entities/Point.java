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

    @Column(name = "code", nullable = true)
    private String code;

    @Column(name = "content", nullable = true)
    private String content;

    @ManyToOne
    @JoinColumn(
            name = "clause_id",
            nullable = false
    )
    private Clause clause;

    private String replaceBy;

    @OneToOne(mappedBy = "point")
    private GraphKnowledge graphKnowledge;
}
