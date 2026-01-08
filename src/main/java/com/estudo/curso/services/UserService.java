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
@Service//indica que a classe é um componente de serviço do Spring
public class UserService {
    @Autowired//injeção de dependência do UserRepository
    private UserRepository userRepository;
    
    public List<User> findAll(){
        return userRepository.findAll();
    }//método para buscar todos os usuários
    public User findById(Long id ){//método para buscar um usuário por id
        Optional<User> obj = userRepository.findById(id);//método para buscar um usuário por id
        return obj.get();//retorna o usuário encontrado
    }

    public User insert(User obj){
        return userRepository.save(obj);
    }
    public void delete(Long id){
         userRepository.deleteById(id);
    }
    
    
}
