package com.estudo.curso.user;

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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserResource.class)
@AutoConfigureMockMvc(addFilters = false)
class UserResourceTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private JwtAuthFilter jwtAuthFilter;

    @Test
    void saveWithWeakPasswordAndInvalidEmailReturnsBadRequest() throws Exception {
        String body = "{\"name\":\"\",\"email\":\"invalido\",\"telefone\":\"1\",\"password\":\"123\"}";

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors", Matchers.hasSize(3)));
    }

    @Test
    void saveValidReturnsCreatedWithoutPasswordInResponse() throws Exception {
        UserDTO dto = new UserDTO(1L, "Maria", "maria@example.com", "111");
        given(userService.insert(any())).willReturn(dto);

        String body = "{\"name\":\"Maria\",\"email\":\"maria@example.com\",\"telefone\":\"111\",\"password\":\"senha123456\"}";

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("maria@example.com"))
                .andExpect(jsonPath("$.password").doesNotExist());
    }

    @Test
    void findByIdReturnsNotFound() throws Exception {
        willThrow(new ResourceNotFoundException(99L)).given(userService).findById(99L);

        mockMvc.perform(get("/users/99"))
                .andExpect(status().isNotFound());
    }
}
