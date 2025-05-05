package org.example.questionmodule.api.services.mapper;

import org.example.questionmodule.api.dtos.admin.ChapterDto;
import org.example.questionmodule.api.entities.Chapter;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring", uses = {ChapterMapper.class})
public interface ChapterMapper {
    ChapterDto toAdminDto(Chapter chapter);
    List<ChapterDto> toAdminDtoList(List<Chapter> chapters);
}
