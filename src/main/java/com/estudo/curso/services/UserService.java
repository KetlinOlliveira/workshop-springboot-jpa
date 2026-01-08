/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.estudo.curso.services;
import com.estudo.curso.repositories.UserRepository;
import java.util.List;

import com.estudo.curso.services.exceptions.DataBaseException;
import com.estudo.curso.services.exceptions.ResourceNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import com.estudo.curso.entities.User;
import java.util.Optional;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
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
        return obj.orElseThrow(() -> new ResourceNotFoundException(id)); //retorna o usuário encontrado
    }

    public User insert(User obj){
        return userRepository.save(obj);
    }

    public void delete(Long id){
        try {
            userRepository.deleteById(id);
        }catch(EmptyResultDataAccessException e){
            throw new  ResourceNotFoundException(id);
        }catch(DataIntegrityViolationException e){
            throw new DataBaseException(e.getMessage());
        }
    }

    public User update(Long id, User obj){
        try {
            User entity = userRepository.getReferenceById(id);

            updateData(entity, obj);
            return userRepository.save(entity);
        }catch(EntityNotFoundException e){
            throw new  ResourceNotFoundException(id);
        }
    }

    private void updateData(User entity, User obj) {
        entity.setName(obj.getName());
        entity.setEmail(obj.getEmail());
        entity.setTelefone(obj.getTelefone());
    }


}
