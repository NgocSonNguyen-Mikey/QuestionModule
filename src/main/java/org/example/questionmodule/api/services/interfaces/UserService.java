package org.example.questionmodule.api.services.interfaces;

import org.example.questionmodule.api.dtos.auth.*;

public interface UserService {
    public AuthResponse register(Register user);
    public AuthResponse authenticate(AuthRequest authRequest);
    public String login(LoginRequest loginRequest);
    UserDto getRole();
}
