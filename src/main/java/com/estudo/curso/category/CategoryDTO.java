package com.estudo.curso.category;

public record CategoryDTO (Long id, String name) {
    public CategoryDTO(Category cat){
        this(cat.getId(), cat.getName());
    }
    
}
