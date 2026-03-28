package com.estudo.curso.resources;

import com.estudo.curso.dto.OrderDTO;
import com.estudo.curso.dto.OrderRequestDTO;
import com.estudo.curso.entities.Order;
import com.estudo.curso.services.OrderService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@CrossOrigin("*")
@RestController
@RequestMapping(value = "/orders")
public class OrderResource {

    @Autowired
    private OrderService orderService;

    // ── GET /orders ───────────────────────────────────────────────────────────
    @GetMapping
    public ResponseEntity<List<OrderDTO>> findAll() {
        List<OrderDTO> list = orderService.findAll();
        return ResponseEntity.ok().body(list);
    }

    // ── GET /orders/{id} ──────────────────────────────────────────────────────
    @GetMapping(value = "/{id}")
    public ResponseEntity<OrderDTO> findById(@PathVariable Long id) {
        OrderDTO dto = orderService.findById(id);
        return ResponseEntity.ok().body(dto);
    }

    // ── POST /orders ──────────────────────────────────────────────────────────
    // Insert ainda recebe Order diretamente para manter compatibilidade
    // com o frontend atual (pode ser migrado para RequestDTO futuramente)
    @PostMapping
    public ResponseEntity<OrderDTO> save(@RequestBody Order obj) {
        OrderDTO result = orderService.insert(obj);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                      .path("/{id}")
                      .buildAndExpand(result.getId())
                      .toUri();
        return ResponseEntity.created(uri).body(result);
    }

    // ── DELETE /orders/{id} ───────────────────────────────────────────────────
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        orderService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // ── PUT /orders/{id} ──────────────────────────────────────────────────────
    // Atualiza status via DTO de entrada (só orderStatus é editável)
    @PutMapping(value = "/{id}")
    public ResponseEntity<OrderDTO> update(@PathVariable Long id,
                                           @RequestBody OrderRequestDTO dto) {
        OrderDTO result = orderService.update(id, dto);
        return ResponseEntity.ok().body(result);
    }
}