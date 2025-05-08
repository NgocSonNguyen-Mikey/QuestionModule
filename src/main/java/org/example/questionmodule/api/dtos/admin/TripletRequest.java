package org.example.questionmodule.api.dtos.admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TripletRequest {
    private String subjectId;
    private String relationId;
    private String objectId;

}
