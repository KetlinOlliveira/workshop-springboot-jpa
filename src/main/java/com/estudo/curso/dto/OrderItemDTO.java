package com.estudo.curso.dto;

import com.estudo.curso.entities.OrderItem;

/**
 * DTO de resposta para cada item dentro de um pedido.
 */
public class OrderItemDTO {

    private Long   productId;
    private String productName;
    private Integer quantity;
    private Double  price;
    private Double  subtotal;

    public OrderItemDTO(OrderItem item) {
        this.productId   = item.getProduct().getId();
        this.productName = item.getProduct().getName();
        this.quantity    = item.getQuantity();
        this.price       = item.getPrice();
        this.subtotal    = item.getSubtotal();
    }

    // ── Getters ───────────────────────────────────────────────────────────────
    public Long    getProductId()   { return productId; }
    public String  getProductName() { return productName; }
    public Integer getQuantity()    { return quantity; }
    public Double  getPrice()       { return price; }
    public Double  getSubtotal()    { return subtotal; }
}