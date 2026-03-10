/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.estudo.curso.resources;


import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.estudo.curso.services.OrderService;

import java.net.URI;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import com.estudo.curso.entities.Order;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 *
 * @author ketli
 */
@CrossOrigin("*")
@RestController//indica que essa classe é um recurso REST
@RequestMapping(value = "/orders")//mapeia as requisições para /orders
public class OrderResource {
    
    @Autowired//injeção de dependencia do spring para o serviço
    private OrderService orderService;
    
   @GetMapping//mapeia requisições GET para esse método
    public ResponseEntity<List<Order>> findAll(){//método para buscar todos os pedidos
       List<Order> list = orderService.findAll();
       return ResponseEntity.ok().body(list);//retorna a lista de pedidos com status 200 OK
       
   }
    @GetMapping(value = "/{id}")//mapeia requisições GET para esse método com um id como parâmetro
   public ResponseEntity<Order> findById(@PathVariable Long id){//método para buscar um pedido por id
       Order obj = orderService.findById(id);//busca o pedido pelo serviço
       return ResponseEntity.ok().body(obj);//retorna o pedido com status 200 OK
            
   }

    @PostMapping
    public ResponseEntity<Order> save(@RequestBody Order obj){
        obj = orderService.insert(obj);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(obj.getId()).toUri();
        return ResponseEntity.created(uri).body(obj);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        orderService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<Order> update(@PathVariable Long id, @RequestBody Order obj){
        obj = orderService.update(id, obj);
        return ResponseEntity.ok().body(obj);
    }
   
   
   
    
}
