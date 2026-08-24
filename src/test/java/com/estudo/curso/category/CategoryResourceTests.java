package com.estudo.curso.category;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.estudo.curso.auth.JwtAuthFilter;
import com.estudo.curso.shared.ResourceNotFoundException;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Testes do contrato HTTP (status, JSON, mapeamento de exceção) com o
 * CategoryService mockado. A matriz de autorização por papel já é validada
 * com tokens reais no teste de integração — aqui os filtros de segurança
 * ficam desligados de propósito.
 */
@WebMvcTest(CategoryResource.class)
@AutoConfigureMockMvc(addFilters = false)
class CategoryResourceTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CategoryService categoryService;

    // JwtAuthFilter é um Filter, então @WebMvcTest o instancia mesmo com
    // addFilters=false (que só evita aplicá-lo, não evita criá-lo). Mockado
    // aqui só para satisfazer o contexto — nunca chega a ser invocado.
    @MockitoBean
    private JwtAuthFilter jwtAuthFilter;

    @Test
    void findAllReturnsOkWithList() throws Exception {
        given(categoryService.findAll()).willReturn(List.of(new Category(1L, "Livros")));

        mockMvc.perform(get("/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Livros"));
    }

    @Test
    void findByIdReturnsNotFoundForMissingCategory() throws Exception {
        willThrow(new ResourceNotFoundException(99L)).given(categoryService).findById(99L);

        mockMvc.perform(get("/categories/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Resource not found"));
    }

    @Test
    void saveReturnsCreatedWithLocationHeader() throws Exception {
        Category toSave = new Category(null, "Livros");
        Category saved = new Category(1L, "Livros");
        given(categoryService.insert(any())).willReturn(saved);

        mockMvc.perform(post("/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(toSave)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(header().string("Location", Matchers.containsString("/categories/1")));
    }

    @Test
    void deleteReturnsNoContent() throws Exception {
        mockMvc.perform(delete("/categories/1"))
                .andExpect(status().isNoContent());
    }
}
