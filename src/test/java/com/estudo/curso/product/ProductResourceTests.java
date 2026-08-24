package com.estudo.curso.product;

import com.estudo.curso.auth.JwtAuthFilter;
import com.estudo.curso.shared.DataBaseException;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductResource.class)
@AutoConfigureMockMvc(addFilters = false)
class ProductResourceTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductService productService;

    @MockitoBean
    private JwtAuthFilter jwtAuthFilter;

    @Test
    void findAllReturnsOk() throws Exception {
        ProductDTO dto = new ProductDTO(new Product(1L, "Mouse", "Óptico", 50.0, ""));
        given(productService.findAll()).willReturn(List.of(dto));

        mockMvc.perform(get("/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Mouse"));
    }

    @Test
    void findByIdReturnsNotFound() throws Exception {
        willThrow(new ResourceNotFoundException(99L)).given(productService).findById(99L);

        mockMvc.perform(get("/products/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void saveWithBlankNameAndNegativePriceReturnsBadRequestWithFieldErrors() throws Exception {
        String body = "{\"name\":\"\",\"price\":-10}";

        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[*].fieldName", Matchers.hasItems("name", "price")));
    }

    @Test
    void saveValidReturnsCreated() throws Exception {
        ProductDTO saved = new ProductDTO(new Product(1L, "Teclado", "Mecânico", 250.0, null));
        given(productService.insert(any())).willReturn(saved);

        String body = "{\"name\":\"Teclado\",\"description\":\"Mecânico\",\"price\":250.0}";

        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Teclado"));
    }

    @Test
    void deleteWithIntegrityViolationReturnsBadRequest() throws Exception {
        willThrow(new DataBaseException("referenced by order item")).given(productService).delete(1L);

        mockMvc.perform(delete("/products/1"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Data base error"));
    }
}
