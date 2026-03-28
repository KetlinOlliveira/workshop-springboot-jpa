package com.estudo.curso.dto;

public record CategoryDTO (Long id, String name) {
    public CategoryDTO(com.estudo.curso.entities.Category cat){
        this(cat.getId(), cat.getName());
    }
    
}
