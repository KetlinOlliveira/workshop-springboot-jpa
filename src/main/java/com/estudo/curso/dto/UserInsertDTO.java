package com.estudo.curso.dto;

public record UserInsertDTO(
    Long id,
    String name,
    String email,
    String telefone,
    String password
) {  
}
