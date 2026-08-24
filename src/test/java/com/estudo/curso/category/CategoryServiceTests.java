package com.estudo.curso.category;

import com.estudo.curso.shared.DataBaseException;
import com.estudo.curso.shared.ResourceNotFoundException;
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
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTests {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService service;

    @Test
    void findAllReturnsAllCategories() {
        Category cat1 = new Category(1L, "Livros");
        Category cat2 = new Category(2L, "Eletrônicos");
        given(categoryRepository.findAll()).willReturn(List.of(cat1, cat2));

        List<Category> result = service.findAll();

        assertThat(result).containsExactly(cat1, cat2);
    }

    @Test
    void findByIdReturnsCategoryWhenExists() {
        Category cat = new Category(1L, "Livros");
        given(categoryRepository.findById(1L)).willReturn(Optional.of(cat));

        Category result = service.findById(1L);

        assertThat(result).isSameAs(cat);
    }

    @Test
    void findByIdThrowsResourceNotFoundWhenMissing() {
        given(categoryRepository.findById(99L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void insertSavesAndReturnsCategory() {
        Category toSave = new Category(null, "Livros");
        Category saved = new Category(1L, "Livros");
        given(categoryRepository.save(toSave)).willReturn(saved);

        Category result = service.insert(toSave);

        assertThat(result).isEqualTo(saved);
    }

    @Test
    void deleteRemovesExistingCategory() {
        service.delete(1L);

        verify(categoryRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteThrowsResourceNotFoundWhenMissing() {
        doThrow(EmptyResultDataAccessException.class).when(categoryRepository).deleteById(99L);

        assertThatThrownBy(() -> service.delete(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void deleteThrowsDataBaseExceptionOnIntegrityViolation() {
        doThrow(new DataIntegrityViolationException("referenced by product"))
                .when(categoryRepository).deleteById(1L);

        assertThatThrownBy(() -> service.delete(1L))
                .isInstanceOf(DataBaseException.class);
    }

    @Test
    void updateChangesNameWhenCategoryExists() {
        Category entity = new Category(1L, "Nome antigo");
        Category dto = new Category(null, "Nome novo");
        given(categoryRepository.getReferenceById(1L)).willReturn(entity);
        given(categoryRepository.save(entity)).willReturn(entity);

        Category result = service.update(1L, dto);

        assertThat(result.getName()).isEqualTo("Nome novo");
    }

    @Test
    void updateThrowsResourceNotFoundWhenMissing() {
        willThrow(EntityNotFoundException.class).given(categoryRepository).getReferenceById(99L);

        assertThatThrownBy(() -> service.update(99L, new Category(null, "X")))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(categoryRepository, never()).save(any());
    }
}
