/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.estudo.curso.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.estudo.curso.entities.Order;

/**
 *
 * @author ketli
 */
public interface OrderRepository extends JpaRepository<Order, Long>{
    //interface para operações de banco de dados da entidade Order
}
