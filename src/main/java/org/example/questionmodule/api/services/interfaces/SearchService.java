package org.example.questionmodule.api.services.interfaces;

import org.example.questionmodule.api.dtos.ResponseDto;

import java.io.IOException;

public interface SearchService {
    public ResponseDto process(String sentences);
}
