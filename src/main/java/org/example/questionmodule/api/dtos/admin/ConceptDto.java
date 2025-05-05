package org.example.questionmodule.api.dtos.admin;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConceptDto {
    private String id;

    private String name;

    private String meaning;

    private Set<String> attrs = new HashSet<>();

    private Set<String> keyphrases = new HashSet<>();

    private Set<String> similar = new HashSet<>();
}
