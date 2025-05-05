package org.example.questionmodule.utils.exceptions;

import org.springframework.http.HttpStatus;

import java.util.List;

public class ValidException extends AbstractException {
    public ValidException(String error, List<String> messages) {
        super(error, HttpStatus.BAD_REQUEST,messages);
    }
}
