package com.estudo.curso.user;

public record UserDTO(
    Long id,
    String name,
    String email,
    String telefone
) {
    public UserDTO(User user) {
        this(user.getId(), user.getName(), user.getEmail(), user.getTelefone());
    }
        
}