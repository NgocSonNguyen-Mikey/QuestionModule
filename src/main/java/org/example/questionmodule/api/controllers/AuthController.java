package org.example.questionmodule.api.controllers;

import lombok.RequiredArgsConstructor;
import org.example.questionmodule.api.dtos.auth.LoginRequest;
import org.example.questionmodule.api.dtos.auth.UserDto;
import org.example.questionmodule.api.services.interfaces.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest loginRequest){
        return ResponseEntity.ok(userService.login(loginRequest));
    }

    @GetMapping("/role")
    public ResponseEntity<UserDto> getRole(String token){
        return ResponseEntity.ok(userService.getRole(token));
    }

}
