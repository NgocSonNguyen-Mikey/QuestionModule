package org.example.questionmodule.api.services.mapper;

import org.example.questionmodule.api.dtos.TripleDto;
import org.example.questionmodule.api.dtos.admin.TripletDto;
import org.example.questionmodule.api.entities.Triplet;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = {TripletMapper.class, ConceptMapper.class, RelationMapper.class})
public  interface TripletMapper {

    @Mapping(source = "relation.name", target = "relation")
    @Mapping(source = "subject.name", target = "subject")
    @Mapping(source = "object.name", target = "object")
    TripleDto toDto(Triplet triplet);

    @Mapping(source = "id", target = "id")
    org.example.questionmodule.api.dtos.admin.TripletDto toAdminDto(Triplet triplet);

    List<org.example.questionmodule.api.dtos.admin.TripletDto> toAdminDtoList(List<Triplet> triplets);
}
