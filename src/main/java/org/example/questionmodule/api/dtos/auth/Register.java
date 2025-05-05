package org.example.questionmodule.api.dtos.auth;


import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.logging.log4j.core.config.plugins.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Register {

    @NotBlank(message = "Name MUST not be blank")
    private String fullName;

    @Email(message = "Invalid email")
    private String email;

    @NotBlank(message = "Password MUST not be blank")
    private String password;
}
