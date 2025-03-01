package org.example.questionmodule.utils.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.sql.Timestamp;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ErrorResponse {
    private Timestamp timestamp;
    private HttpStatus httpStatus;
    private Integer statusCode;
    private String error;
    private List<String> messages;
}
