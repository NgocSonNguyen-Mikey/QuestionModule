package org.example.questionmodule.api.dtos.admin;

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
public class RelationDto {
    private String id;

    private String name;

    private String meaning;

    private Set<String> similar = new HashSet<>();

    private Set<String> keyword = new HashSet<>();

}
