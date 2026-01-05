package com.estudo.curso.entities;

import jakarta.persistence.*;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

@Entity
@Table(name = "tb_payment")
public class Payment implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Instant moment;

    @OneToOne
    @MapsId
    private Order order;

    public Payment(){

    }
    public Payment(Long id, Instant instant, Order order){
        this.id = id;
        this.moment = instant;
        this.order = order;

    }

    public Long getId() {
        return id;
    }

    public Payment setId(Long id) {
        this.id = id;
        return this;
    }

    public Order getOrder() {
        return order;
    }

    public Payment setOrder(Order order) {
        this.order = order;
        return this;
    }

    public Instant getMoment() {
        return moment;
    }

    public Payment setMoment(Instant moment) {
        this.moment = moment;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Payment payment)) return false;
        return Objects.equals(id, payment.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
