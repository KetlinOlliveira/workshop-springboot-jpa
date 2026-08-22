package com.estudo.curso.services;

import com.estudo.curso.dto.OrderDTO;
import com.estudo.curso.dto.OrderInsertDTO;
import com.estudo.curso.dto.OrderRequestDTO;
import com.estudo.curso.entities.Order;
import com.estudo.curso.entities.OrderItem;
import com.estudo.curso.entities.OrderStatus;
import com.estudo.curso.entities.Product;
import com.estudo.curso.entities.User;
import com.estudo.curso.repositories.OrderRepository;
import com.estudo.curso.repositories.ProductRepository;
import com.estudo.curso.repositories.UserRepository;
import com.estudo.curso.services.exceptions.DataBaseException;
import com.estudo.curso.services.exceptions.ResourceNotFoundException;

import jakarta.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

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

    // ── Insert: recebe apenas cliente + itens (produto/quantidade). O preço de
    // cada item é sempre lido do Product cadastrado, nunca do request. ────────
    public OrderDTO insert(OrderInsertDTO dto) {
        User client = userRepository.findById(dto.clientId())
                                     .orElseThrow(() -> new ResourceNotFoundException(dto.clientId()));

        Order order = new Order(null, Instant.now(), OrderStatus.WAITING_PAYMENT, client);

        for (OrderInsertDTO.OrderItemInsertDTO itemDto : dto.items()) {
            Product product = productRepository.findById(itemDto.productId())
                                                .orElseThrow(() -> new ResourceNotFoundException(itemDto.productId()));
            order.getItems().add(new OrderItem(order, product, product.getPrice(), itemDto.quantity()));
        }

        order = orderRepository.save(order);
        return new OrderDTO(order);
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
