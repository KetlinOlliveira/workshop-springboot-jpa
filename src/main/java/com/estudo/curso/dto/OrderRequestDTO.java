package com.estudo.curso.dto;

import com.estudo.curso.entities.OrderStatus;
import com.fasterxml.jackson.annotation.JsonSetter;

/**
 * DTO de entrada para atualizar o status de um pedido.
 * Aceita o status como String e realiza conversão para enum.
 */
public class OrderRequestDTO {

    private OrderStatus orderStatus;

    public OrderRequestDTO() {}

    public OrderRequestDTO(String orderStatusStr) {
        if (orderStatusStr != null) {
            try {
                this.orderStatus = OrderStatus.valueOf(orderStatusStr);
            } catch (IllegalArgumentException e) {
                System.err.println("Status inválido: " + orderStatusStr);
            }
        }
    }

    @JsonSetter("orderStatus")
    public void setOrderStatusFromString(String orderStatusStr) {
        if (orderStatusStr != null) {
            try {
                this.orderStatus = OrderStatus.valueOf(orderStatusStr);
            } catch (IllegalArgumentException e) {
                System.err.println("Status inválido: " + orderStatusStr);
            }
        }
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }
}