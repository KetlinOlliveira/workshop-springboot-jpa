/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.estudo.curso.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.estudo.curso.entities.User;

/**
 *
 * @author ketli
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    //interface para operações de banco de dados da entidade User
}
