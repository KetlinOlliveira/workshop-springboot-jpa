package com.estudo.curso.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.estudo.curso.entities.Order;

public interface OrderRepository extends JpaRepository<Order, Long>{
    //interface para operações de banco de dados da entidade Order
}
