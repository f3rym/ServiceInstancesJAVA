package ferym.project.service;

import ferym.project.dto.UserDto;
import ferym.project.mapper.UserMapper;
import ferym.project.model.User;
import ferym.project.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private UserMapper userMapper;

    @InjectMocks private UserService userService;

    @Test
    void getAll_ShouldReturnUsers() {
        when(userRepository.findAll()).thenReturn(List.of(new User()));
        when(userMapper.toDto(any())).thenReturn(new UserDto());
        assertThat(userService.getAll()).hasSize(1);
    }

    @Test
    void getById_ShouldThrowException_WhenNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> userService.getById(99L))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void create_ShouldSaveAndReturnUser() {
        UserDto dto = new UserDto();
        when(userMapper.toEntity(dto)).thenReturn(new User());
        when(userRepository.save(any())).thenReturn(new User());
        when(userMapper.toDto(any())).thenReturn(new UserDto());

        assertThat(userService.create(dto)).isNotNull();
        verify(userRepository).save(any(User.class));
    }

    @Test
    void update_ShouldUpdateUsername_WhenUserExists() {
        User existingUser = new User();
        UserDto updateDto = new UserDto();
        updateDto.setUsername("NewName");

        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(userMapper.toDto(existingUser)).thenReturn(updateDto);

        userService.update(1L, updateDto);
        assertThat(existingUser.getUsername()).isEqualTo("NewName");
        verify(userRepository).findById(1L);
    }

    @Test
    void delete_ShouldThrowException_WhenUserDoesNotExist() {
        when(userRepository.existsById(1L)).thenReturn(false);

        assertThatThrownBy(() -> userService.delete(1L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("не найден");

        verify(userRepository, never()).deleteById(any());
    }

    @Test
    void delete_ShouldDelete_WhenUserExists() {
        when(userRepository.existsById(1L)).thenReturn(true);
        userService.delete(1L);
        verify(userRepository).deleteById(1L);
    }
}