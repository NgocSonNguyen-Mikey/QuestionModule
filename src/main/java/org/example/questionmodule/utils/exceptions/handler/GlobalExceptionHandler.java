package org.example.questionmodule.utils.exceptions.handler;

import org.example.questionmodule.utils.dtos.ErrorResponse;
import org.example.questionmodule.utils.exceptions.AbstractException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.Timestamp;

@RestControllerAdvice
//@Log4j2
public class GlobalExceptionHandler {
    @ExceptionHandler(AbstractException.class)
    public ResponseEntity<ErrorResponse> handleException(final AbstractException exception) {
//        log.error("Message error: {}", exception.getMessages().get(0));
        return buildResponse(exception);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleException(final DataIntegrityViolationException exception) {
        var status = HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status).body(
                ErrorResponse.builder()
                        .timestamp(new Timestamp(System.currentTimeMillis()))
                        .httpStatus(status)
                        .statusCode(status.value())
                        .error("Dữ liệu bị trùng lắp !")
                        .build()
        );
    }


    public ResponseEntity<ErrorResponse> buildResponse(AbstractException e) {
        return ResponseEntity.status(e.getStatus().value()).body(
                ErrorResponse.builder()
                        .timestamp(new Timestamp(System.currentTimeMillis()))
                        .httpStatus(e.getStatus())
                        .statusCode(e.getStatus().value())
                        .error(e.getError())
                        .messages(e.getMessages())
                        .build()
        );
    }
}