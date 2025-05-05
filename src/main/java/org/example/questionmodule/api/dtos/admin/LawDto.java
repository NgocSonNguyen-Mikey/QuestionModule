package org.example.questionmodule.api.dtos.admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LawDto {
    private String id;

    private Date year;

    private String name;

    private List<ChapterDto> chapters;
}
