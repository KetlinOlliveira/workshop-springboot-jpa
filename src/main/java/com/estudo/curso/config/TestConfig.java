/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.estudo.curso.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import com.estudo.curso.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import com.estudo.curso.entities.User;
import java.util.Arrays;

/**
 *
 * @author ketli
 */
@Configuration
@Profile("test")
public class TestConfig implements CommandLineRunner {
    @Autowired
    private UserRepository userRepository;
  
    @Override
    public void run(String... args) throws Exception {
         User user1 = new User(null, "Maria", "mari@gmail.com", "279999", "senhaMaria");
         User user2 = new User(null, "João ", "joao@gmail.com", "88888", "senhaJoaõ");
       
         userRepository.saveAll(Arrays.asList(user1, user2));
    }
    
    
}
