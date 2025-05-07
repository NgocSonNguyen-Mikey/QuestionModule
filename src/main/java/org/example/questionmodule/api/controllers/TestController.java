package org.example.questionmodule.api.controllers;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;



@RestController
@RequestMapping("/test")
public class TestController {

    @GetMapping("/free")
    public String testLoginFeature() {
        System.out.println("testLoginFeature");
        return "Success free";
    }

    @GetMapping("/user")
    @PreAuthorize("isAuthenticated()")
    public String testUserFeature() {
        System.out.println("testUserFeature");
        return "Success user";
    }

    @GetMapping("/admin")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public String testAdminFeature() {
        System.out.println("testAdminFeature");
        return "Success admin";
    }
}
