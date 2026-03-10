package com.estudo.curso.services;

import com.estudo.curso.repositories.ProductRepository;
import com.estudo.curso.services.exceptions.DataBaseException;
import com.estudo.curso.services.exceptions.ResourceNotFoundException;

import jakarta.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
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

     public Product insert(Product obj){
        return productRepository.save(obj);
    }

    public void delete(Long id){
        try {
            productRepository.deleteById(id);
        }catch(EmptyResultDataAccessException e){
            throw new  ResourceNotFoundException(id);
        }catch(DataIntegrityViolationException e){
            throw new DataBaseException(e.getMessage());
        }
    }
    
    public Product update(Long id, Product obj){
        try {
            Product entity = productRepository.getReferenceById(id);
            updateData(entity, obj);
            return productRepository.save(entity);
        }catch(EntityNotFoundException e){
            throw new  ResourceNotFoundException(id);
        }
    }

     private void updateData(Product entity, Product obj) {
        entity.setName(obj.getName());
    }
}
