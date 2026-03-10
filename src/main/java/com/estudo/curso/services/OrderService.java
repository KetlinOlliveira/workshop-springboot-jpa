/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.estudo.curso.services;
import com.estudo.curso.repositories.OrderRepository;
import com.estudo.curso.services.exceptions.DataBaseException;
import com.estudo.curso.services.exceptions.ResourceNotFoundException;

import jakarta.persistence.EntityNotFoundException;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import com.estudo.curso.entities.Order;
import com.estudo.curso.entities.OrderItem;

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

    public Order insert(Order obj){
    // Para cada item que veio do Frontend
    for (OrderItem x : obj.getItems()) {
        //diz ao item que ele pertence a este Pedido 'obj'
        x.setOrder(obj); 
    }
        return orderRepository.save(obj);
    }

    public void delete(Long id){
        try {
            orderRepository.deleteById(id);
        }catch(EmptyResultDataAccessException e){
            throw new  ResourceNotFoundException(id);
        }catch(DataIntegrityViolationException e){
            throw new DataBaseException(e.getMessage());
        }
    }
    
    public Order update(Long id, Order obj){
        try {
            Order entity = orderRepository.getReferenceById(id);
            updateData(entity, obj);
            return orderRepository.save(entity);
        }catch(EntityNotFoundException e){
            throw new  ResourceNotFoundException(id);
        }
    }

     private void updateData(Order entity, Order obj) {
       if (obj.getOrderStatus() != null) {
        entity.setOrderStatus(obj.getOrderStatus());
    }
    }

}
