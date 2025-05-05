package org.example.questionmodule.api.services.mapper;

import org.example.questionmodule.api.entities.User;
import org.example.questionmodule.api.dtos.auth.Register;
import org.example.questionmodule.api.dtos.auth.UserResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface UserMapper {
    @Mapping(source = "email", target = "username")
    User toEntity(Register user);

    @Mapping(source = "role.name", target = "role")
    UserResponse toResponse(User user);
}
