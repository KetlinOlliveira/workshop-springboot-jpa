package com.estudo.curso.services;

import com.estudo.curso.dto.OrderDTO;
import com.estudo.curso.dto.OrderRequestDTO;
import com.estudo.curso.entities.Order;
import com.estudo.curso.entities.OrderItem;
import com.estudo.curso.repositories.OrderRepository;
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
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    // ── Retorna lista de DTO ──────────────────────────────────────────────────
    public List<OrderDTO> findAll() {
        return orderRepository.findAll()
                              .stream()
                              .map(OrderDTO::new)
                              .collect(Collectors.toList());
    }

    // ── Retorna DTO de um pedido pelo id ──────────────────────────────────────
    public OrderDTO findById(Long id) {
        Optional<Order> od = orderRepository.findById(id);
        Order entity = od.orElseThrow(() -> new ResourceNotFoundException(id));
        return new OrderDTO(entity);
    }

    // ── Insert: ainda recebe a entidade Order do frontend ─────────────────────
    // (mantido igual ao original para não quebrar o frontend existente)
    public OrderDTO insert(Order obj) {
        for (OrderItem x : obj.getItems()) {
            x.setOrder(obj);
        }
        obj = orderRepository.save(obj);
        return new OrderDTO(obj);
    }

    // ── Deleta pedido ─────────────────────────────────────────────────────────
    public void delete(Long id) {
        try {
            orderRepository.deleteById(id);
        } catch (EmptyResultDataAccessException e) {
            throw new ResourceNotFoundException(id);
        } catch (DataIntegrityViolationException e) {
            throw new DataBaseException(e.getMessage());
        }
    }

    // ── Atualiza status via DTO de entrada ────────────────────────────────────
    public OrderDTO update(Long id, OrderRequestDTO dto) {
        try {
            Order entity = orderRepository.getReferenceById(id);
            if (dto.getOrderStatus() != null) {
                entity.setOrderStatus(dto.getOrderStatus());
            }
            entity = orderRepository.save(entity);
            return new OrderDTO(entity);
        } catch (EntityNotFoundException e) {
            throw new ResourceNotFoundException(id);
        }
    }
}