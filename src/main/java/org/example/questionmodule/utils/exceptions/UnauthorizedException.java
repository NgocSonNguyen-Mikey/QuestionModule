package org.example.questionmodule.utils.exceptions;

import org.springframework.http.HttpStatus;

import java.util.List;

public class UnauthorizedException extends AbstractException{
    public UnauthorizedException(List<String> messages) {
        super("Unauthorized server error!", HttpStatus.UNAUTHORIZED, messages);
    }
}
