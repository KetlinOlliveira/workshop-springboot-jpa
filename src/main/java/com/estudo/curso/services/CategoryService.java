package com.estudo.curso.services;


import com.estudo.curso.entities.Category;
import com.estudo.curso.repositories.CategoryRepository;
import com.estudo.curso.repositories.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;


    public List<Category> findAll(){
        return categoryRepository.findAll();

    }
    public Category findById(Long id){
        Optional<Category> cate = categoryRepository.findById(id);
        return cate.get();
    }



}
