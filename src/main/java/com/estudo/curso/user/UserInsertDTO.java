package com.estudo.curso.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserInsertDTO(
    @NotBlank(message = "Nome é obrigatório") String name,
    @NotBlank(message = "E-mail é obrigatório") @Email(message = "E-mail inválido") String email,
    String telefone,
    @NotBlank(message = "Senha é obrigatória") @Size(min = 6, message = "A senha deve ter ao menos 6 caracteres") String password
) {
}
