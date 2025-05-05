package org.example.questionmodule.api.services.mapper;

import org.example.questionmodule.api.dtos.LawDto;
import org.example.questionmodule.api.entities.Law;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring", uses = {PointMapper.class})
public interface LawMapper {
    LawDto toDto(Law law);

    org.example.questionmodule.api.dtos.admin.LawDto toAdminDto(Law law);

    List<org.example.questionmodule.api.dtos.admin.LawDto> toAdminDtoList(List<Law> law);
}
