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
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.*;

import static org.aspectj.weaver.tools.cache.SimpleCacheFactory.path;

@Component
@RequiredArgsConstructor
public class AuthFilter extends OncePerRequestFilter {

    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws IOException, ServletException {

        // Viet them 1 api login, moi khi login tren fe se goi api nay de them user vo db neu trong db chua co, filter nay bo qua api do

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof JwtAuthenticationToken jwtAuthToken) {
            Jwt jwt = jwtAuthToken.getToken();
            String subject = jwt.getSubject();
            var user = userDetailsService.loadUserByUsername(subject); //Chỗ này tim theo id, sau do xai xac thuc truoc moi ham trong controller, coi TestController de biet them, trong db phai luu id cua user trong clerk
            JwtAuthenticationToken newAuth = new JwtAuthenticationToken(jwt, user.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(newAuth);
        }
        filterChain.doFilter(request, response);
    }
}
