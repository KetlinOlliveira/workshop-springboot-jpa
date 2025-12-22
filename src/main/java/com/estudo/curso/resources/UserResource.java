/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.estudo.curso.resources;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.estudo.curso.entities.User;
import org.springframework.web.bind.annotation.GetMapping;
/**
 *
 * @author ketli
 */

@RestController
@RequestMapping(value = "/users")
public class UserResource {
    
    @GetMapping
    public ResponseEntity<User> findAll(){
    User u = new User(1L, "aria", "aria@GMAIL.com","99999", "senha"  );
    return ResponseEntity.ok().body(u);
    }
   
}
