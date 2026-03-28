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

import com.estudo.curso.dto.UserDTO;
import com.estudo.curso.dto.UserInsertDTO;
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
    
    public List<UserDTO> findAll(){
        return userRepository.findAll()
        .stream().
        map(UserDTO::new)
        .toList();
    }

    public UserDTO findById(Long id ){//método para buscar um usuário por id
       User obj = userRepository.findById(id)
       .orElseThrow(() -> new ResourceNotFoundException(id));
       return new UserDTO(obj);
    }
    public UserDTO insert(UserInsertDTO obj){
        User entity = new User(null, obj.name(), obj.email(), obj.telefone(), obj.password());
        entity = userRepository.save(entity);
        return new UserDTO(userRepository.save(entity));
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

 public UserDTO update(Long id, UserInsertDTO dto) {
    try {
        User entity = userRepository.getReferenceById(id);
        entity.setName(dto.name());
        entity.setEmail(dto.email());
        entity.setTelefone(dto.telefone());
        return new UserDTO(userRepository.save(entity));
    } catch (EntityNotFoundException e) {
        throw new ResourceNotFoundException(id);
    }
}

    private void updateData(User entity, User obj) {
        entity.setName(obj.getName());
        entity.setEmail(obj.getEmail());
        entity.setTelefone(obj.getTelefone());
    }


}
