package com.estudo.curso.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.List;

/**
 * DTO de entrada para criar um pedido. O cliente só informa produto e
 * quantidade — o preço de cada item é sempre lido do Product no servidor,
 * nunca aceito do request (evita o cliente forjar o valor do pedido).
 */
public record OrderInsertDTO(
    @NotNull(message = "Cliente é obrigatório") Long clientId,
    @NotEmpty(message = "O pedido precisa ter ao menos um item") @Valid List<OrderItemInsertDTO> items
) {

    public record OrderItemInsertDTO(
        @NotNull(message = "Produto é obrigatório") Long productId,
        @NotNull(message = "Quantidade é obrigatória") @Positive(message = "Quantidade deve ser maior que zero") Integer quantity
    ) {
    }
}
