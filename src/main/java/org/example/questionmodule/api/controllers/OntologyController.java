package org.example.questionmodule.api.controllers;

import lombok.RequiredArgsConstructor;
import org.example.questionmodule.api.services.interfaces.OntologyService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ontology")
@RequiredArgsConstructor
public class OntologyController {
    private final OntologyService ontologyService;

    @GetMapping()
    public String test(){
        ontologyService.test();
        return "success";
    }
}
