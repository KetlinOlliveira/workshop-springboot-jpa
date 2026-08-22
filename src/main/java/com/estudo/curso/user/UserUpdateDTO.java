package com.estudo.curso.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO de atualização de usuário. A senha é opcional: quando omitida (ou em
 * branco), a senha atual é preservada; quando enviada, precisa atender o
 * tamanho mínimo e é regravada com hash.
 */
public record UserUpdateDTO(
    @NotBlank(message = "Nome é obrigatório") String name,
    @NotBlank(message = "E-mail é obrigatório") @Email(message = "E-mail inválido") String email,
    String telefone,
    @Size(min = 6, message = "A senha deve ter ao menos 6 caracteres") String password
) {
}
