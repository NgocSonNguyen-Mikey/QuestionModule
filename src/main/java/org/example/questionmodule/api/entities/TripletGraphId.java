package org.example.questionmodule.api.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TripletGraphId implements Serializable {
    @Column(name = "triplet_id")
    private String tripletId;

    @Column(name = "graph_id")
    private String graphId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TripletGraphId)) return false;
        TripletGraphId that = (TripletGraphId) o;
        return Objects.equals(tripletId, that.tripletId) &&
                Objects.equals(graphId, that.graphId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tripletId, graphId);
    }
}
