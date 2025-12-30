/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.estudo.curso.config;

import com.estudo.curso.entities.Category;
import com.estudo.curso.entities.OrderStatus;
import com.estudo.curso.repositories.CategoryRepository;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import com.estudo.curso.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import com.estudo.curso.entities.User;
import com.estudo.curso.repositories.OrderRepository;
import java.util.Arrays;
import com.estudo.curso.entities.Order;
import java.time.Instant;
//classe para popular o banco de dados com dados iniciais para teste
/**
 *
 * @author ketli
 */
@Configuration//indica que é uma classe de configuração do spring
@Profile("test")//indica que essa configuração só vai ser ativada quando o perfil de teste estiver ativo
public class TestConfig implements CommandLineRunner {
    @Autowired//faz a injeção de dependencia do spring para o repositorio
    private UserRepository userRepository;//repositorio de usuário
    @Autowired
    private OrderRepository orderRepository;//repositorio de pedido
    @Autowired
    private CategoryRepository categoryRepository;//repositorio de categoria
    
    @Override
    public void run(String... args) throws Exception {
         User user1 = new User(null, "Maria", "mari@gmail.com", "279999", "senhaMaria");
         User user2 = new User(null, "João ", "joao@gmail.com", "88888", "senhaJoaõ");
          Order o1 = new Order(null, Instant.parse("2019-06-20T19:53:07Z"), OrderStatus.WAITING_PAYMENT, user1);
          Order o2 = new Order(null, Instant.parse("2019-07-21T03:42:10Z"),OrderStatus.PAID, user1);
          Order o3 = new Order(null, Instant.parse("2019-07-22T15:21:22Z"),OrderStatus.DELIVERED, user2);

        Category cat1 = new Category(null, "Electronics");
        Category cat2 = new Category(null, "Books");
        Category cat3 = new Category(null, "Computers");

        categoryRepository.saveAll(Arrays.asList(cat1, cat2, cat3));
         userRepository.saveAll(Arrays.asList(user1, user2));
          orderRepository.saveAll(Arrays.asList(o1, o2, o3));
        }

    
}
