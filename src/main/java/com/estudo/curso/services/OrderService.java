/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.estudo.curso.services;
import com.estudo.curso.repositories.OrderRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.estudo.curso.entities.Order;
import java.util.Optional;

/**
 *
 * @author ketli
 */
@Service
public class OrderService {
    @Autowired
    private OrderRepository orderRepository;
    
    public List<Order> findAll(){
        return orderRepository.findAll();
    }//método para buscar todos os pedidos
    public Order findById (Long id){
         Optional<Order> od = orderRepository.findById(id);//método para buscar um pedido por id
         return od.get();//retorna o pedido encontrado
    }
}
