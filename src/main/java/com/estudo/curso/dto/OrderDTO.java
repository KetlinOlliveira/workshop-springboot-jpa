package com.estudo.curso.dto;

import com.estudo.curso.entities.Order;
import com.estudo.curso.entities.OrderStatus;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

/**
 * DTO de resposta para Order.
 * Agrega os itens já como OrderItemDTO e expõe o status como String legível.
 */
public class OrderDTO {

    private Long        id;
    private Instant     moment;
    private String      orderStatus;   // ex: "WAITING_PAYMENT", "PAID", etc.
    private Long        clientId;
    private String      clientName;
    private List<OrderItemDTO> items;
    private Double      total;
    // Payment pode ser nulo se ainda não foi pago
    private Instant     paymentMoment;

    // ── Construtor a partir da entidade ──────────────────────────────────────
    public OrderDTO(Order order) {
        this.id          = order.getId();
        this.moment      = order.getMoment();
        this.orderStatus = order.getOrderStatus() != null
                           ? order.getOrderStatus().name()
                           : null;
        this.clientId    = order.getClient() != null ? order.getClient().getId()   : null;
        this.clientName  = order.getClient() != null ? order.getClient().getName() : null;
        this.items       = order.getItems()
                                .stream()
                                .map(OrderItemDTO::new)
                                .collect(Collectors.toList());
        this.total        = order.getTotal();
        this.paymentMoment = (order.getPayment() != null)
                              ? order.getPayment().getMoment()
                              : null;
    }

    // ── Getters ───────────────────────────────────────────────────────────────
    public Long               getId()            { return id; }
    public Instant            getMoment()        { return moment; }
    public String             getOrderStatus()   { return orderStatus; }
    public Long               getClientId()      { return clientId; }
    public String             getClientName()    { return clientName; }
    public List<OrderItemDTO> getItems()         { return items; }
    public Double             getTotal()         { return total; }
    public Instant            getPaymentMoment() { return paymentMoment; }
}