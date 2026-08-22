package com.estudo.curso.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.estudo.curso.entities.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    //interface para operações de banco de dados da entidade User
}
