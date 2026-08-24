package com.estudo.curso.user;

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
import org.springframework.security.crypto.password.PasswordEncoder;

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
class UserServiceTests {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private UserService service;

    @Test
    void findAllReturnsAllUsersAsDto() {
        User user = new User(1L, "Maria", "maria@example.com", "111", "hash");
        given(userRepository.findAll()).willReturn(List.of(user));

        List<UserDTO> result = service.findAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).email()).isEqualTo("maria@example.com");
    }

    @Test
    void findByIdReturnsDtoWhenExists() {
        User user = new User(1L, "Maria", "maria@example.com", "111", "hash");
        given(userRepository.findById(1L)).willReturn(Optional.of(user));

        UserDTO result = service.findById(1L);

        assertThat(result.name()).isEqualTo("Maria");
    }

    @Test
    void findByIdThrowsResourceNotFoundWhenMissing() {
        given(userRepository.findById(99L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void insertHashesPasswordAndAssignsClientRole() {
        UserInsertDTO dto = new UserInsertDTO("Maria", "maria@example.com", "111", "senhaCrua");
        Role clientRole = new Role(1L, "ROLE_CLIENT");
        given(passwordEncoder.encode("senhaCrua")).willReturn("senhaHasheada");
        given(roleRepository.findByAuthority("ROLE_CLIENT")).willReturn(Optional.of(clientRole));

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        given(userRepository.save(captor.capture())).willAnswer(invocation -> {
            User u = invocation.getArgument(0);
            u.setId(1L);
            return u;
        });

        UserDTO result = service.insert(dto);

        User saved = captor.getValue();
        assertThat(saved.getPassword()).isEqualTo("senhaHasheada");
        assertThat(saved.getRoles()).containsExactly(clientRole);
        assertThat(result.id()).isEqualTo(1L);
    }

    @Test
    void insertThrowsWhenClientRoleMissing() {
        UserInsertDTO dto = new UserInsertDTO("Maria", "maria@example.com", "111", "senhaCrua");
        given(passwordEncoder.encode("senhaCrua")).willReturn("senhaHasheada");
        given(roleRepository.findByAuthority("ROLE_CLIENT")).willReturn(Optional.empty());

        assertThatThrownBy(() -> service.insert(dto))
                .isInstanceOf(IllegalStateException.class);

        verify(userRepository, never()).save(any());
    }

    @Test
    void deleteRemovesExistingUser() {
        service.delete(1L);

        verify(userRepository).deleteById(1L);
    }

    @Test
    void deleteThrowsResourceNotFoundWhenMissing() {
        doThrow(EmptyResultDataAccessException.class).when(userRepository).deleteById(99L);

        assertThatThrownBy(() -> service.delete(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void deleteThrowsDataBaseExceptionOnIntegrityViolation() {
        doThrow(new DataIntegrityViolationException("referenced by order"))
                .when(userRepository).deleteById(1L);

        assertThatThrownBy(() -> service.delete(1L))
                .isInstanceOf(DataBaseException.class);
    }

    @Test
    void updateWithBlankPasswordKeepsExistingPassword() {
        User entity = new User(1L, "Maria", "maria@example.com", "111", "senhaAntigaHasheada");
        UserUpdateDTO dto = new UserUpdateDTO("Maria Nova", "maria2@example.com", "222", null);
        given(userRepository.getReferenceById(1L)).willReturn(entity);
        given(userRepository.save(entity)).willReturn(entity);

        UserDTO result = service.update(1L, dto);

        assertThat(result.name()).isEqualTo("Maria Nova");
        assertThat(entity.getPassword()).isEqualTo("senhaAntigaHasheada");
        verify(passwordEncoder, never()).encode(any());
    }

    @Test
    void updateWithNewPasswordHashesIt() {
        User entity = new User(1L, "Maria", "maria@example.com", "111", "senhaAntigaHasheada");
        UserUpdateDTO dto = new UserUpdateDTO("Maria", "maria@example.com", "111", "senhaNovaCrua");
        given(userRepository.getReferenceById(1L)).willReturn(entity);
        given(passwordEncoder.encode("senhaNovaCrua")).willReturn("senhaNovaHasheada");
        given(userRepository.save(entity)).willReturn(entity);

        service.update(1L, dto);

        assertThat(entity.getPassword()).isEqualTo("senhaNovaHasheada");
    }

    @Test
    void updateThrowsResourceNotFoundWhenMissing() {
        willThrow(EntityNotFoundException.class).given(userRepository).getReferenceById(99L);

        assertThatThrownBy(() -> service.update(99L, new UserUpdateDTO("X", "x@example.com", "1", null)))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(userRepository, never()).save(any());
    }
}
