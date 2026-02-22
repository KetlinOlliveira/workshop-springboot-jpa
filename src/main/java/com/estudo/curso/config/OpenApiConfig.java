package com.estudo.curso.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenApi(){
        return new OpenAPI().info(new Info()
                .title("API REST - Sistema de pedidos")
                .description("Api desenvolvida durante o workshop de Spring Boot e JPA. Gerencia usuários, produtos, pedidos e categorias.")
                .version("v1.0.0")
                .contact(new Contact()
                        .name("Ketlin Olliveira")
                        .url("https://github.com/KetlinOlliveira")
                        .email("KetlinOliveira20044@gmail.com")));

    }
}
