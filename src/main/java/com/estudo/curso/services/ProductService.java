package com.estudo.curso.services;

import com.estudo.curso.dto.ProductDTO;
import com.estudo.curso.dto.ProductRequestDTO;
import com.estudo.curso.entities.Product;
import com.estudo.curso.repositories.ProductRepository;
import com.estudo.curso.services.exceptions.DataBaseException;
import com.estudo.curso.services.exceptions.ResourceNotFoundException;

import jakarta.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    // ── Retorna lista de DTO (não expõe entidade JPA) ─────────────────────────
    public List<ProductDTO> findAll() {
        return productRepository.findAll()
                                .stream()
                                .map(ProductDTO::new)
                                .collect(Collectors.toList());
    }

    // ── Retorna DTO de um produto pelo id ─────────────────────────────────────
    public ProductDTO findById(Long id) {
        Optional<Product> prod = productRepository.findById(id);
        Product entity = prod.orElseThrow(() -> new ResourceNotFoundException(id));
        return new ProductDTO(entity);
    }

    // ── Cria produto a partir do DTO de entrada ───────────────────────────────
    public ProductDTO insert(ProductRequestDTO dto) {
        Product obj = new Product();
        obj.setName(dto.getName());
        obj.setDescription(dto.getDescription());
        obj.setPrice(dto.getPrice());
        obj.setImgUrl(dto.getImgUrl());
        obj = productRepository.save(obj);
        return new ProductDTO(obj);
    }

    // ── Deleta produto ────────────────────────────────────────────────────────
    public void delete(Long id) {
        try {
            productRepository.deleteById(id);
        } catch (EmptyResultDataAccessException e) {
            throw new ResourceNotFoundException(id);
        } catch (DataIntegrityViolationException e) {
            throw new DataBaseException(e.getMessage());
        }
    }

    // ── Atualiza produto a partir do DTO de entrada ───────────────────────────
    public ProductDTO update(Long id, ProductRequestDTO dto) {
        try {
            Product entity = productRepository.getReferenceById(id);
            updateData(entity, dto);
            entity = productRepository.save(entity);
            return new ProductDTO(entity);
        } catch (EntityNotFoundException e) {
            throw new ResourceNotFoundException(id);
        }
    }

    // ── Método interno de atualização ─────────────────────────────────────────
    private void updateData(Product entity, ProductRequestDTO dto) {
        if (dto.getName()        != null) entity.setName(dto.getName());
        if (dto.getDescription() != null) entity.setDescription(dto.getDescription());
        if (dto.getPrice()       != null) entity.setPrice(dto.getPrice());
        if (dto.getImgUrl()      != null) entity.setImgUrl(dto.getImgUrl());
    }
}