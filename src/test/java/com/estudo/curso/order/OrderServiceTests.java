package com.estudo.curso.order;

import com.estudo.curso.product.Product;
import com.estudo.curso.product.ProductRepository;
import com.estudo.curso.shared.DataBaseException;
import com.estudo.curso.shared.ResourceNotFoundException;
import com.estudo.curso.user.User;
import com.estudo.curso.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willAnswer;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class OrderServiceTests {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private OrderService service;

    @Test
    void findAllReturnsAllOrdersAsDto() {
        User client = new User(1L, "Maria", "maria@example.com", "111", "hash");
        Order order = new Order(1L, java.time.Instant.now(), OrderStatus.WAITING_PAYMENT, client);
        given(orderRepository.findAll()).willReturn(List.of(order));

        List<OrderDTO> result = service.findAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getClientName()).isEqualTo("Maria");
    }

    @Test
    void findByIdThrowsResourceNotFoundWhenMissing() {
        given(orderRepository.findById(99L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void insertComputesPriceFromProductNotFromRequest() {
        User client = new User(1L, "Maria", "maria@example.com", "111", "hash");
        Product product = new Product(10L, "Mouse", "Óptico", 50.0, "");
        OrderInsertDTO dto = new OrderInsertDTO(1L, List.of(new OrderInsertDTO.OrderItemInsertDTO(10L, 3)));

        given(userRepository.findById(1L)).willReturn(Optional.of(client));
        given(productRepository.findById(10L)).willReturn(Optional.of(product));
        willAnswer(invocation -> invocation.getArgument(0)).given(orderRepository).save(any());

        OrderDTO result = service.insert(dto);

        assertThat(result.getItems()).hasSize(1);
        assertThat(result.getItems().get(0).getPrice()).isEqualTo(50.0);
        assertThat(result.getItems().get(0).getQuantity()).isEqualTo(3);
        assertThat(result.getTotal()).isEqualTo(150.0);
    }

    @Test
    void insertThrowsResourceNotFoundWhenClientMissing() {
        OrderInsertDTO dto = new OrderInsertDTO(99L, List.of(new OrderInsertDTO.OrderItemInsertDTO(10L, 1)));
        given(userRepository.findById(99L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> service.insert(dto))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(orderRepository, never()).save(any());
    }

    @Test
    void insertThrowsResourceNotFoundWhenProductMissing() {
        User client = new User(1L, "Maria", "maria@example.com", "111", "hash");
        OrderInsertDTO dto = new OrderInsertDTO(1L, List.of(new OrderInsertDTO.OrderItemInsertDTO(99L, 1)));
        given(userRepository.findById(1L)).willReturn(Optional.of(client));
        given(productRepository.findById(99L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> service.insert(dto))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(orderRepository, never()).save(any());
    }

    @Test
    void deleteRemovesExistingOrder() {
        service.delete(1L);

        verify(orderRepository).deleteById(1L);
    }

    @Test
    void deleteThrowsResourceNotFoundWhenMissing() {
        doThrow(EmptyResultDataAccessException.class).when(orderRepository).deleteById(99L);

        assertThatThrownBy(() -> service.delete(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void deleteThrowsDataBaseExceptionOnIntegrityViolation() {
        doThrow(new DataIntegrityViolationException("referenced by payment"))
                .when(orderRepository).deleteById(1L);

        assertThatThrownBy(() -> service.delete(1L))
                .isInstanceOf(DataBaseException.class);
    }

    @Test
    void updateChangesStatusWhenProvided() {
        User client = new User(1L, "Maria", "maria@example.com", "111", "hash");
        Order entity = new Order(1L, java.time.Instant.now(), OrderStatus.WAITING_PAYMENT, client);
        OrderRequestDTO dto = new OrderRequestDTO();
        dto.setOrderStatus(OrderStatus.PAID);
        given(orderRepository.getReferenceById(1L)).willReturn(entity);
        given(orderRepository.save(entity)).willReturn(entity);

        OrderDTO result = service.update(1L, dto);

        assertThat(result.getOrderStatus()).isEqualTo("PAID");
    }

    @Test
    void updateKeepsStatusWhenNotProvided() {
        User client = new User(1L, "Maria", "maria@example.com", "111", "hash");
        Order entity = new Order(1L, java.time.Instant.now(), OrderStatus.PAID, client);
        given(orderRepository.getReferenceById(1L)).willReturn(entity);
        given(orderRepository.save(entity)).willReturn(entity);

        OrderDTO result = service.update(1L, new OrderRequestDTO());

        assertThat(result.getOrderStatus()).isEqualTo("PAID");
    }

    @Test
    void updateThrowsResourceNotFoundWhenMissing() {
        willThrow(EntityNotFoundException.class).given(orderRepository).getReferenceById(99L);

        assertThatThrownBy(() -> service.update(99L, new OrderRequestDTO()))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(orderRepository, never()).save(any());
    }
}
