package com.estudo.curso.auth;

import com.estudo.curso.shared.StanderError;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;

/**
 * Traduz negações de autorização detectadas no filtro de segurança (rota
 * protegida por role via SecurityConfig) para o mesmo formato de erro
 * (StanderError) usado pelo resto da API.
 */
@Component
public class JsonAccessDeniedHandler implements AccessDeniedHandler {

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException)
            throws IOException {
        HttpStatus status = HttpStatus.FORBIDDEN;
        StanderError err = new StanderError(Instant.now(), status.value(), "Forbidden",
                "Você não tem permissão para acessar este recurso", request.getRequestURI());
        response.setStatus(status.value());
        response.setContentType("application/json");
        response.getWriter().write(objectMapper.writeValueAsString(err));
    }
}
