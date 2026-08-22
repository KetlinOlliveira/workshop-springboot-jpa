package com.estudo.curso.config;

import com.estudo.curso.user.Role;
import com.estudo.curso.user.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * Garante que os papéis básicos existam em qualquer ambiente (dev, test,
 * produção), já que não há Flyway/Liquibase neste projeto ainda. Roda antes
 * do TestConfig (@Order(2)), que depende de ROLE_CLIENT já existir.
 */
@Component
@Order(1)
public class RoleSeeder implements CommandLineRunner {

    @Autowired
    private RoleRepository roleRepository;

    @Override
    public void run(String... args) {
        createIfMissing("ROLE_CLIENT");
        createIfMissing("ROLE_ADMIN");
    }

    private void createIfMissing(String authority) {
        if (roleRepository.findByAuthority(authority).isEmpty()) {
            roleRepository.save(new Role(null, authority));
        }
    }
}
