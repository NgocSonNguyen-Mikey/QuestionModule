package org.example.questionmodule.api.services.interfaces;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface LoadDocService {
    public String loadDoc(MultipartFile file) throws IOException;
}
