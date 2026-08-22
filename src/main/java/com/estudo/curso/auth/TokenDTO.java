package com.estudo.curso.auth;

public record TokenDTO(String token, long expiresIn) {
}
