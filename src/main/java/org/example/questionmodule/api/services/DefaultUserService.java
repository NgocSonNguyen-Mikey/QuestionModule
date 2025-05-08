package org.example.questionmodule.api.services;

import lombok.RequiredArgsConstructor;
import org.example.questionmodule.api.dtos.auth.LoginRequest;
import org.example.questionmodule.api.entities.Role;
import org.example.questionmodule.api.entities.User;
import org.example.questionmodule.api.repositories.RoleRepository;
import org.example.questionmodule.api.repositories.UserRepository;
import org.example.questionmodule.api.services.interfaces.UserService;
import org.example.questionmodule.api.services.mapper.UserMapper;
import org.example.questionmodule.api.dtos.auth.AuthRequest;
import org.example.questionmodule.api.dtos.auth.AuthResponse;
import org.example.questionmodule.api.dtos.auth.Register;
import org.example.questionmodule.utils.exceptions.DataNotFoundException;
import org.example.questionmodule.utils.exceptions.InternalServerException;
import org.example.questionmodule.utils.service.JwtService;
import org.example.questionmodule.utils.validate.ObjectsValidator;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DefaultUserService implements UserService {

    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    private final UserMapper userMapper;

    private final JwtService jwtService;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;

    private final ObjectsValidator<AuthRequest> authValidator;


    @Override
    public AuthResponse register(Register user) {
        User userEntity = userMapper.toEntity(user);
//        userEntity.setCreateDate(AppUtil.getDateNow());
        userEntity.setPassword(passwordEncoder.encode(user.getPassword()));
        Role roleEntity = roleRepository.findById(2)
                .orElseThrow(() -> new InternalServerException(
                        List.of("Role is not exist")
                ));
        userEntity.setRole(roleEntity);
        userRepository.save(userEntity);
        var userResponse = userMapper.toResponse(userEntity);
        return AuthResponse.builder()
                .token(jwtService.generateToken(userEntity))
                .user(userResponse)
                .build();
    }

    @Override
    public AuthResponse authenticate(AuthRequest authRequest) {
        authValidator.validate(authRequest);
        var user = userRepository.findByUsername(authRequest.getUsername())
                .orElseThrow(() -> new DataNotFoundException( List.of("User is not exits")));
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        user.getUsername(),
                        authRequest.getPassword()
                )
        );
        var token = jwtService.generateToken(user);
        return AuthResponse.builder()
                .token(token)
                .user(userMapper.toResponse(user))
                .build();
    }

    @Override
    public String login(LoginRequest loginRequest) {
        var user = userRepository.findById(loginRequest.getId());
        if(user.isPresent()) return "success";
        userRepository.save(User.builder()
                        .id(loginRequest.getId())
                        .fullName(loginRequest.getFullname())
                .build());
        return "success";
    }

    //    @Override
    public Boolean checkPassword(String token, String password) {
        var userEntity = getUserById(jwtService.extractSubject(jwtService.validateToken(token)));
        return passwordEncoder.matches(password, userEntity.getPassword());
    }

    private User getUserById(String userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException( List.of("User is not exits")));
    }

}
