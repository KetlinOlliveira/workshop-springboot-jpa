package com.estudo.curso.auth;

import com.estudo.curso.shared.StanderError;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;

/**
 * Traduz falhas de autenticação detectadas no filtro de segurança (token
 * ausente/inválido em rota protegida) para o mesmo formato de erro
 * (StanderError) usado pelo resto da API.
 */
@Component
public class JsonAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
            throws IOException {
        HttpStatus status = HttpStatus.UNAUTHORIZED;
        StanderError err = new StanderError(Instant.now(), status.value(), "Unauthorized",
                "Autenticação necessária para acessar este recurso", request.getRequestURI());
        response.setStatus(status.value());
        response.setContentType("application/json");
        response.getWriter().write(objectMapper.writeValueAsString(err));
    }
}
