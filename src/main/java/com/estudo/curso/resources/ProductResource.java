package com.estudo.curso.resources;

import com.estudo.curso.dto.ProductDTO;
import com.estudo.curso.dto.ProductRequestDTO;
import com.estudo.curso.services.ProductService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@CrossOrigin("*")
@RestController
@RequestMapping(value = "/products")
public class ProductResource {

    @Autowired
    private ProductService productService;

    // ── GET /products ─────────────────────────────────────────────────────────
    @GetMapping
    public ResponseEntity<List<ProductDTO>> findAll() {
        List<ProductDTO> list = productService.findAll();
        return ResponseEntity.ok().body(list);
    }

    // ── GET /products/{id} ────────────────────────────────────────────────────
    @GetMapping(value = "/{id}")
    public ResponseEntity<ProductDTO> findById(@PathVariable Long id) {
        ProductDTO dto = productService.findById(id);
        return ResponseEntity.ok().body(dto);
    }

    // ── POST /products ────────────────────────────────────────────────────────
    // Recebe ProductRequestDTO — nunca a entidade JPA
    @PostMapping
    public ResponseEntity<ProductDTO> save(@RequestBody ProductRequestDTO dto) {
        ProductDTO result = productService.insert(dto);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                      .path("/{id}")
                      .buildAndExpand(result.getId())
                      .toUri();
        return ResponseEntity.created(uri).body(result);
    }

    // ── DELETE /products/{id} ─────────────────────────────────────────────────
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        productService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // ── PUT /products/{id} ────────────────────────────────────────────────────
    // Recebe ProductRequestDTO para atualizar
    @PutMapping(value = "/{id}")
    public ResponseEntity<ProductDTO> update(@PathVariable Long id,
                                             @RequestBody ProductRequestDTO dto) {
        ProductDTO result = productService.update(id, dto);
        return ResponseEntity.ok().body(result);
    }
}