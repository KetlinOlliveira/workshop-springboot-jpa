/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.estudo.curso.resources;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.estudo.curso.entities.User;
import com.estudo.curso.services.UserService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
/**
 *
 * @author ketli
 */

@RestController
@RequestMapping(value = "/users")
public class UserResource {
    @Autowired
    private UserService userService;
    
    @GetMapping
    public ResponseEntity<List<User>> findAll(){
        List<User> list = userService.findAll();
        
        return ResponseEntity.ok().body(list);
    }
    
    @GetMapping(value = "/{id}")
    public ResponseEntity<User> findById(@PathVariable Long id){
        User obj = userService.findById(id);
        
        return ResponseEntity.ok().body(obj);
        
    }
   
}
