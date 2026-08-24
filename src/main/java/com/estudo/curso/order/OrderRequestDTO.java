package com.estudo.curso.order;

import com.fasterxml.jackson.annotation.JsonSetter;

/**
 * DTO de entrada para atualizar o status de um pedido.
 * Aceita o status como String (ex.: "PAID") e converte para enum — um valor
 * inválido propaga a exceção para o Jackson, virando 400 em vez de ser
 * silenciosamente ignorado.
 */
public class OrderRequestDTO {

    private OrderStatus orderStatus;

    public OrderRequestDTO() {}

    @JsonSetter("orderStatus")
    public void setOrderStatusFromString(String orderStatusStr) {
        if (orderStatusStr != null) {
            this.orderStatus = OrderStatus.valueOf(orderStatusStr);
        }
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }
}
