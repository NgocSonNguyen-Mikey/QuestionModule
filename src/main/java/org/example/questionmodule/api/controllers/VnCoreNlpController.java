package org.example.questionmodule.api.controllers;

import lombok.RequiredArgsConstructor;
import org.example.questionmodule.api.entities.Triplet;
import org.example.questionmodule.api.services.SearchService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.example.questionmodule.api.services.VNCoreNlpService;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/vncorenlp")
@RequiredArgsConstructor
public class VnCoreNlpController {
    private final VNCoreNlpService vnCoreNlpService;
    private final SearchService searchService;

//    @PostMapping("/analyze")
//    public String analyzeText(@RequestBody String text) {
//        return vnCoreNlpService.analyzeText(text);
//    }
    @PostMapping("/analyze")
    public List<Triplet> search(@RequestBody String text) throws IOException {
        return searchService.getGraph();
    }
}
