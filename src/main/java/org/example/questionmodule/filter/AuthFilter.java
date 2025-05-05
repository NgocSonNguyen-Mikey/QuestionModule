package org.example.questionmodule.filter;

import com.clerk.backend_api.helpers.jwks.AuthenticateRequest;
import com.clerk.backend_api.helpers.jwks.AuthenticateRequestOptions;
import com.clerk.backend_api.helpers.jwks.RequestState;
import groovyjarjarantlr4.v4.runtime.misc.NotNull;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.impl.DefaultClaims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.questionmodule.api.entities.Role;
import org.example.questionmodule.api.entities.User;
import org.example.questionmodule.api.repositories.RoleRepository;
import org.example.questionmodule.api.repositories.UserRepository;
import org.example.questionmodule.utils.exceptions.InternalServerException;
import org.example.questionmodule.utils.service.JwtService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.*;

import static org.aspectj.weaver.tools.cache.SimpleCacheFactory.path;

@Component
@RequiredArgsConstructor
public class AuthFilter extends OncePerRequestFilter {

    @Value("${clerk.api.secret-key}")
    private String clerkApiSecretKey;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws IOException, ServletException {

        String path = request.getRequestURI();

        if (path.startsWith("/api")) {
            filterChain.doFilter(request, response); // Cho phép tiếp tục request
            return; // Không làm gì thêm với request này
        }

        try {

            Map<String, List<String>> headers = new HashMap<>();
            request.getHeaderNames().asIterator().forEachRemaining(headerName -> {
                List<String> headerValues = new ArrayList<>();
                request.getHeaders(headerName).asIterator().forEachRemaining(headerValues::add);
                headers.put(headerName, headerValues);
            });

            // authenticate with clerk API
            RequestState state = AuthenticateRequest.authenticateRequest(headers,
                    AuthenticateRequestOptions.Builder.withSecretKey(clerkApiSecretKey).build()
            );


            if (!state.isSignedIn()){
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.addHeader("Content-Type", "application/json");
                response.getWriter().write("{\"detail\": \"" + state.reason().get().message() + "\"}");


            } else {
                Map<String, Object> claims = state.claims().orElse(new DefaultClaims());
                String userId = (String) claims.get("sub");
                String email = (String) claims.get("email");
                String name = (String) claims.getOrDefault("name", email);

                // Tìm hoặc tạo User trong DB
                User user = userRepository.findById(userId).orElseGet(() -> {
                    Role roleEntity = roleRepository.findById(2)
                            .orElseThrow(() -> new InternalServerException(
                                    List.of("Role is not exist")
                            ));
                    User newUser = new User();
                    newUser.setId(userId);
                    newUser.setUsername(email);
                    newUser.setFullName(name);
                    newUser.setRole(roleEntity); // gán mặc định
                    return userRepository.save(newUser);
                });

                // Gán vào Spring Security context
                Authentication authentication = new UsernamePasswordAuthenticationToken(user.getId(), null, new ArrayList<>());
                SecurityContextHolder.getContext().setAuthentication(authentication);
                filterChain.doFilter(request, response);
            }

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.addHeader("Content-Type", "application/json");
            response.getWriter().write("{\"detail\": \"Unable to authenticate request\"}");
        }

    }
}
