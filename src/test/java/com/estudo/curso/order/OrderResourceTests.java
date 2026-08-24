package com.estudo.curso.order;

import com.estudo.curso.auth.JwtAuthFilter;
import com.estudo.curso.shared.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.BDDMockito.willThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderResource.class)
@AutoConfigureMockMvc(addFilters = false)
class OrderResourceTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OrderService orderService;

    @MockitoBean
    private JwtAuthFilter jwtAuthFilter;

    @Test
    void saveWithEmptyItemsReturnsBadRequest() throws Exception {
        String body = "{\"clientId\":1,\"items\":[]}";

        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].fieldName").value("items"));
    }

    @Test
    void saveMissingClientIdReturnsBadRequest() throws Exception {
        String body = "{\"items\":[{\"productId\":1,\"quantity\":1}]}";

        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].fieldName").value("clientId"));
    }

    @Test
    void findByIdReturnsNotFound() throws Exception {
        willThrow(new ResourceNotFoundException(99L)).given(orderService).findById(99L);

        mockMvc.perform(get("/orders/99"))
                .andExpect(status().isNotFound());
    }
}
