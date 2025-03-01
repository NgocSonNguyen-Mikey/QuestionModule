package org.example.questionmodule.api.entities;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Date;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Setter
@Getter
@Entity
@Table(name = "laws")
public class Law {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false, name = "year")
    private Date year;

    @Column(nullable = false, name = "name")
    private String name;

    @OneToMany(
            mappedBy = "law",
            fetch = FetchType.LAZY
    )
    private List<Chapter> chapters;
}
