package com.estudo.curso.services;

import com.estudo.curso.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.estudo.curso.entities.Product;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    public List<Product> findAll(){
      return productRepository.findAll();
    }
    public Product findById(Long id){
        Optional<Product> prod = productRepository.findById(id);
        return prod.get();
    }
}
