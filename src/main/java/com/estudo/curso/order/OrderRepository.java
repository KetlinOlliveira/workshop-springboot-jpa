package com.estudo.curso.order;

import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long>{
    //interface para operações de banco de dados da entidade Order
}
