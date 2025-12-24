/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.estudo.curso.services;
import com.estudo.curso.repositories.UserRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import com.estudo.curso.entities.User;
import java.util.Optional;
import org.springframework.stereotype.Service;

/**
 *
 * @author ketli
 */
@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    
    public List<User> findAll(){
        return userRepository.findAll();
    }
    public User findById(Long id ){
        Optional<User> obj = userRepository.findById(id);
        return obj.get();
    }
    
    
}
