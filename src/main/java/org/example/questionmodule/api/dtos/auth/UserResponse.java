package org.example.questionmodule.api.dtos.auth;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse {
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String id;
    @JsonProperty("full_name")
    private String fullName;
    private String email;
    @JsonProperty("date_of_birth")
    private String dateOfBirth;
    @JsonProperty("phone_number")
    private String phoneNumber;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String role;
}
