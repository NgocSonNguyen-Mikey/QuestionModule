package org.example.questionmodule.api.controllers;

import lombok.RequiredArgsConstructor;
import org.example.questionmodule.api.dtos.ResponseDto;
import org.example.questionmodule.api.services.VNCoreNlpService;
import org.example.questionmodule.api.services.interfaces.LoadDocService;
import org.example.questionmodule.api.services.interfaces.SearchService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import vn.pipeline.Sentence;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
public class SearchController {
    private final SearchService searchService;

    private final VNCoreNlpService vnCoreNlpService;

//    @PreAuthorize("hasRole('USER')")
    @GetMapping("")
    public ResponseDto search(@RequestParam String question) throws IOException {
        return searchService.process(question);
    }

}
