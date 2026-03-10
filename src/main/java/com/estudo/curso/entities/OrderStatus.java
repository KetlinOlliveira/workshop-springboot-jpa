package com.estudo.curso.entities;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum OrderStatus {//enumeração para os status do pedido
    WAITING_PAYMENT(1),
    PAID(2),
    SHIPPED(3),
    DELIVERED(4),
    CANCELED(5);

    private int code;

    private OrderStatus(int code){
        this.code = code;
    }

    @JsonValue
    public int getCode(){
        return code;
    }

    @JsonCreator
    public static OrderStatus valueOf(int code){//método para retornar o status do pedido a partir do código
        for(OrderStatus value: OrderStatus.values()){
            if(value.getCode() == code){//compara o código com os valores do enum
                return value;//retorna o valor correspondente
            }
        }
        throw new IllegalArgumentException("Valor inválido");//lança uma exceção se o código for inválido
    }
}
