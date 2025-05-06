package org.example.questionmodule.api.controllers;

import lombok.RequiredArgsConstructor;
import org.example.questionmodule.api.services.interfaces.LoadDocService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/load")
@RequiredArgsConstructor
public class LoadDocController {
    private final LoadDocService loadDocService;

    @PostMapping()
    public ResponseEntity<String> test(@RequestParam("file") MultipartFile file) throws IOException {
        loadDocService.loadDoc(file);
        return ResponseEntity.ok("Success");
    }
}
