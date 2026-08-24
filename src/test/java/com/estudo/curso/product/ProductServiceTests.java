package com.estudo.curso.product;

import com.estudo.curso.shared.DataBaseException;
import com.estudo.curso.shared.ResourceNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
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
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ProductServiceTests {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService service;

    @Test
    void findAllReturnsAllProductsAsDto() {
        Product p1 = new Product(1L, "Mouse", "Óptico", 50.0, "");
        given(productRepository.findAll()).willReturn(List.of(p1));

        List<ProductDTO> result = service.findAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(1L);
        assertThat(result.get(0).getName()).isEqualTo("Mouse");
    }

    @Test
    void findByIdReturnsDtoWhenExists() {
        Product product = new Product(1L, "Mouse", "Óptico", 50.0, "");
        given(productRepository.findById(1L)).willReturn(Optional.of(product));

        ProductDTO result = service.findById(1L);

        assertThat(result.getName()).isEqualTo("Mouse");
    }

    @Test
    void findByIdThrowsResourceNotFoundWhenMissing() {
        given(productRepository.findById(99L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void insertBuildsEntityFromDtoAndSaves() {
        ProductRequestDTO dto = new ProductRequestDTO();
        dto.setName("Teclado");
        dto.setDescription("Mecânico");
        dto.setPrice(250.0);
        dto.setImgUrl("img.png");

        ArgumentCaptor<Product> captor = ArgumentCaptor.forClass(Product.class);
        given(productRepository.save(captor.capture())).willAnswer(invocation -> {
            Product p = invocation.getArgument(0);
            p.setId(1L);
            return p;
        });

        ProductDTO result = service.insert(dto);

        Product saved = captor.getValue();
        assertThat(saved.getName()).isEqualTo("Teclado");
        assertThat(saved.getDescription()).isEqualTo("Mecânico");
        assertThat(saved.getPrice()).isEqualTo(250.0);
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Teclado");
    }

    @Test
    void deleteRemovesExistingProduct() {
        service.delete(1L);

        verify(productRepository).deleteById(1L);
    }

    @Test
    void deleteThrowsResourceNotFoundWhenMissing() {
        doThrow(EmptyResultDataAccessException.class).when(productRepository).deleteById(99L);

        assertThatThrownBy(() -> service.delete(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void deleteThrowsDataBaseExceptionOnIntegrityViolation() {
        doThrow(new DataIntegrityViolationException("referenced by order item"))
                .when(productRepository).deleteById(1L);

        assertThatThrownBy(() -> service.delete(1L))
                .isInstanceOf(DataBaseException.class);
    }

    @Test
    void updateOnlyChangesFieldsPresentInDto() {
        Product entity = new Product(1L, "Mouse", "Óptico", 50.0, "old.png");
        ProductRequestDTO dto = new ProductRequestDTO();
        dto.setPrice(70.0);
        // name, description e imgUrl ficam null no dto: devem permanecer inalterados
        given(productRepository.getReferenceById(1L)).willReturn(entity);
        given(productRepository.save(entity)).willReturn(entity);

        ProductDTO result = service.update(1L, dto);

        assertThat(result.getName()).isEqualTo("Mouse");
        assertThat(result.getDescription()).isEqualTo("Óptico");
        assertThat(result.getImgUrl()).isEqualTo("old.png");
        assertThat(result.getPrice()).isEqualTo(70.0);
    }

    @Test
    void updateThrowsResourceNotFoundWhenMissing() {
        willThrow(EntityNotFoundException.class).given(productRepository).getReferenceById(99L);

        assertThatThrownBy(() -> service.update(99L, new ProductRequestDTO()))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(productRepository, never()).save(any());
    }
}
