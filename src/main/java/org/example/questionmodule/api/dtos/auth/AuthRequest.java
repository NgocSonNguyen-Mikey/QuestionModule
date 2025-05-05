package org.example.questionmodule.api.dtos.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthRequest {

    @Email(message = "Invalid email")
    private String username;

    @Size(min = 6, message = "Password must have at least 6 characters")
    private String password;
}
