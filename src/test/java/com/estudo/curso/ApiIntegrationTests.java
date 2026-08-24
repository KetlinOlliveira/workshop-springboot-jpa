package com.estudo.curso;

import com.estudo.curso.auth.LoginDTO;
import com.estudo.curso.auth.TokenDTO;
import com.estudo.curso.category.Category;
import com.estudo.curso.order.OrderInsertDTO;
import com.estudo.curso.product.ProductRequestDTO;
import com.estudo.curso.user.Role;
import com.estudo.curso.user.RoleRepository;
import com.estudo.curso.user.User;
import com.estudo.curso.user.UserInsertDTO;
import com.estudo.curso.user.UserRepository;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Teste de integração ponta a ponta: sobe o contexto completo (Security com
 * JWT real, controllers, services, JPA) contra um Postgres descartável via
 * Testcontainers — não usa o Postgres do docker-compose de desenvolvimento.
 * Os métodos são ordenados de propósito: cada um monta o cenário em cima do
 * estado deixado pelo anterior (registro → login → promoção a admin →
 * catálogo → pedido), como uma jornada real de uso da API.
 */
@Testcontainers
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ApiIntegrationTests {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16");

    @Autowired
    private TestRestTemplate rest;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    private String clientToken;
    private String adminToken;
    private Long clientUserId;
    private String client2Token;
    private Long client2UserId;
    private Long productId;
    private Long orderId;

    @Test
    @Order(1)
    void registersAndLogsInAsClient() {
        UserInsertDTO registration = new UserInsertDTO("Cliente Um", "cliente1@integration.test", "111", "senha123456");
        ResponseEntity<String> registerResponse = rest.postForEntity("/users", registration, String.class);
        assertThat(registerResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        clientUserId = extractIdFromLocation(registerResponse);

        ResponseEntity<TokenDTO> loginResponse = rest.postForEntity(
                "/auth/login", new LoginDTO("cliente1@integration.test", "senha123456"), TokenDTO.class);

        assertThat(loginResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        clientToken = loginResponse.getBody().token();
        assertThat(clientToken).isNotBlank();
    }

    @Test
    @Order(2)
    void loginWithWrongPasswordReturnsUnauthorized() {
        ResponseEntity<String> response = rest.postForEntity(
                "/auth/login", new LoginDTO("cliente1@integration.test", "senhaErrada"), String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    @Order(3)
    void publicCatalogIsReachableWithoutToken() {
        ResponseEntity<Category[]> response = rest.getForEntity("/categories", Category[].class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @Order(4)
    void anonymousRequestToProtectedRouteReturnsUnauthorized() {
        ResponseEntity<String> response = rest.exchange(
                "/categories", HttpMethod.POST, new HttpEntity<>(new Category(null, "X")), String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    @Order(5)
    void clientWithoutAdminRoleCannotCreateCategory() {
        ResponseEntity<String> response = rest.exchange(
                "/categories", HttpMethod.POST, authorizedEntity(new Category(null, "Eletrônicos"), clientToken), String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    @Order(6)
    void promotingToAdminUnlocksManagementRoutes() {
        Role adminRole = roleRepository.findByAuthority("ROLE_ADMIN").orElseThrow();
        User client = userRepository.findById(clientUserId).orElseThrow();
        client.getRoles().add(adminRole);
        userRepository.save(client);

        // O token antigo não carrega a nova role: é preciso logar de novo.
        ResponseEntity<TokenDTO> loginResponse = rest.postForEntity(
                "/auth/login", new LoginDTO("cliente1@integration.test", "senha123456"), TokenDTO.class);
        adminToken = loginResponse.getBody().token();

        ResponseEntity<Category> categoryResponse = rest.exchange(
                "/categories", HttpMethod.POST, authorizedEntity(new Category(null, "Eletrônicos"), adminToken), Category.class);
        assertThat(categoryResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        ProductRequestDTO productDto = new ProductRequestDTO();
        productDto.setName("Mouse");
        productDto.setDescription("Óptico");
        productDto.setPrice(50.0);
        ResponseEntity<String> productResponse = rest.exchange(
                "/products", HttpMethod.POST, authorizedEntity(productDto, adminToken), String.class);

        assertThat(productResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        productId = readLongField(productResponse.getBody(), "$.id");
    }

    @Test
    @Order(7)
    void registersSecondClientThatStaysNonAdmin() {
        // cliente1 virou admin no passo anterior — os testes de "usuário comum"
        // dali pra frente usam esse segundo cliente, que nunca é promovido.
        UserInsertDTO registration = new UserInsertDTO("Cliente Dois", "cliente2@integration.test", "222", "senha123456");
        ResponseEntity<String> registerResponse = rest.postForEntity("/users", registration, String.class);
        assertThat(registerResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        client2UserId = extractIdFromLocation(registerResponse);

        ResponseEntity<TokenDTO> loginResponse = rest.postForEntity(
                "/auth/login", new LoginDTO("cliente2@integration.test", "senha123456"), TokenDTO.class);
        client2Token = loginResponse.getBody().token();
        assertThat(client2Token).isNotBlank();
    }

    @Test
    @Order(8)
    void authenticatedClientCanCreateOrderWithPriceComputedFromProduct() {
        OrderInsertDTO dto = new OrderInsertDTO(client2UserId, List.of(new OrderInsertDTO.OrderItemInsertDTO(productId, 2)));

        ResponseEntity<String> response = rest.exchange(
                "/orders", HttpMethod.POST, authorizedEntity(dto, client2Token), String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        String body = response.getBody();
        assertThat((Number) JsonPath.read(body, "$.total")).isEqualTo(100.0);
        assertThat((String) JsonPath.read(body, "$.orderStatus")).isEqualTo("WAITING_PAYMENT");
        orderId = readLongField(body, "$.id");
    }

    @Test
    @Order(9)
    void clientCannotChangeOrderStatusButAdminCan() {
        // Enviado como JSON cru (e não um OrderRequestDTO serializado pelo Jackson):
        // OrderStatus tem @JsonValue no código numérico, então serializar o enum
        // Java diretamente manda o número, não o nome — e a API só aceita o nome
        // (ex.: "PAID") nesse campo. É assim que um cliente real chama a rota.
        String statusUpdate = "{\"orderStatus\":\"PAID\"}";

        ResponseEntity<String> forbidden = rest.exchange(
                "/orders/" + orderId, HttpMethod.PUT, authorizedJsonEntity(statusUpdate, client2Token), String.class);
        assertThat(forbidden.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);

        ResponseEntity<String> allowed = rest.exchange(
                "/orders/" + orderId, HttpMethod.PUT, authorizedJsonEntity(statusUpdate, adminToken), String.class);
        assertThat(allowed.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat((String) JsonPath.read(allowed.getBody(), "$.orderStatus")).isEqualTo("PAID");
    }

    @Test
    @Order(10)
    void ownerCanSeeOwnProfileButNotSomeoneElses() {
        ResponseEntity<String> forbidden = rest.exchange(
                "/users/" + clientUserId, HttpMethod.GET, authorizedEntity(null, client2Token), String.class);
        assertThat(forbidden.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);

        ResponseEntity<String> allowedSelf = rest.exchange(
                "/users/" + client2UserId, HttpMethod.GET, authorizedEntity(null, client2Token), String.class);
        assertThat(allowedSelf.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    private <T> HttpEntity<T> authorizedEntity(T body, String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        return new HttpEntity<>(body, headers);
    }

    private HttpEntity<String> authorizedJsonEntity(String rawJsonBody, String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
        return new HttpEntity<>(rawJsonBody, headers);
    }

    private Long extractIdFromLocation(ResponseEntity<?> response) {
        String location = response.getHeaders().getLocation().toString();
        return Long.valueOf(location.substring(location.lastIndexOf('/') + 1));
    }

    private Long readLongField(String json, String path) {
        return ((Number) JsonPath.read(json, path)).longValue();
    }
}
