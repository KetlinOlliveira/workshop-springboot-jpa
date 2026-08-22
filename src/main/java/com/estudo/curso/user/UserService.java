package com.estudo.curso.user;

import java.util.List;

import com.estudo.curso.shared.DataBaseException;
import com.estudo.curso.shared.ResourceNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

@Service//indica que a classe é um componente de serviço do Spring
public class UserService {
    @Autowired//injeção de dependência do UserRepository
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

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
        User entity = new User();
        entity.setName(obj.name());
        entity.setEmail(obj.email());
        entity.setTelefone(obj.telefone());
        entity.setPassword(passwordEncoder.encode(obj.password()));
        entity = userRepository.save(entity);
        return new UserDTO(entity);
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

    public UserDTO update(Long id, UserUpdateDTO dto) {
        try {
            User entity = userRepository.getReferenceById(id);
            entity.setName(dto.name());
            entity.setEmail(dto.email());
            entity.setTelefone(dto.telefone());
            if (dto.password() != null && !dto.password().isBlank()) {
                entity.setPassword(passwordEncoder.encode(dto.password()));
            }
            return new UserDTO(userRepository.save(entity));
        } catch (EntityNotFoundException e) {
            throw new ResourceNotFoundException(id);
        }
    }

}
