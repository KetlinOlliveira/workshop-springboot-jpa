package com.estudo.curso.entities;

import com.estudo.curso.entities.pk.OrderItemPK;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "tb_order_item ")
public class OrderItem  implements Serializable {
    private static final long serialVersionUID = 1L;

    @EmbeddedId
    private OrderItemPK id = new OrderItemPK();

    private Integer quantity;
    private Double price;


    public OrderItem(){
    }
    public OrderItem(Order order, Product product,Double price, Integer quantity) {
        id.setOrder(order);
        id.setProduct(product);
        this.price = price;
        this.quantity = quantity;
    }

    @JsonIgnoreProperties("items")
    public Order getOrder(){
        return id.getOrder();

    }
    public void setOrder(Order order){
        id.setOrder(order);
    }

    @JsonIgnoreProperties("items")
    public Product getProduct(){
        return id.getProduct();

    }
    public void setProduct(Product product){
        id.setProduct(product);

    }
    @JsonIgnore
    public OrderItemPK getId() {
        return id;
    }

    public void setId(OrderItemPK id) {
        this.id = id;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
    public Double getSubtotal(){
       return price * quantity;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof OrderItem orderItem)) return false;
        return Objects.equals(id, orderItem.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
