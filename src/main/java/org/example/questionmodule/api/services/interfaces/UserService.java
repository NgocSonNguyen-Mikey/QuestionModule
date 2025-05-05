package org.example.questionmodule.api.services.interfaces;

import org.example.questionmodule.api.dtos.auth.AuthRequest;
import org.example.questionmodule.api.dtos.auth.AuthResponse;
import org.example.questionmodule.api.dtos.auth.Register;

public interface UserService {
    public AuthResponse register(Register user);
    public AuthResponse authenticate(AuthRequest authRequest);
}
