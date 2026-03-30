package ferym.project.service;

import ferym.project.dto.UserDto;
import ferym.project.mapper.UserMapper;
import ferym.project.model.User;
import ferym.project.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private UserMapper userMapper;

    @InjectMocks private UserService userService;

    @Test
    void getAll_ShouldReturnList() {
        User user = new User();
        UserDto dto = new UserDto();
        when(userRepository.findAll()).thenReturn(List.of(user));
        when(userMapper.toDto(any(User.class))).thenReturn(dto);

        List<UserDto> result = userService.getAll();

        assertThat(result).hasSize(1);
        verify(userRepository).findAll();
    }

    @Test
    void getById_ShouldReturnDto_WhenFound() {
        User user = new User();
        UserDto dto = new UserDto();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userMapper.toDto(any(User.class))).thenReturn(dto);

        UserDto result = userService.getById(1L);

        assertThat(result).isNotNull();
    }

    @Test
    void getById_ShouldThrow_WhenNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getById(1L))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void create_ShouldSaveAndReturnDto() {
        User user = new User();
        UserDto dto = new UserDto();
        when(userMapper.toEntity(dto)).thenReturn(user);
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toDto(any(User.class))).thenReturn(dto);

        UserDto result = userService.create(dto);

        assertThat(result).isEqualTo(dto);
        verify(userRepository).save(user);
    }

    @Test
    void update_ShouldUpdateUsername_WhenFound() {
        User user = new User();
        user.setUsername("old_nick");

        UserDto dto = new UserDto();
        dto.setUsername("new_nick");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userMapper.toDto(any(User.class))).thenReturn(dto);

        UserDto result = userService.update(1L, dto);

        assertThat(result.getUsername()).isEqualTo("new_nick");

    }

    @Test
    void update_ShouldThrow_WhenNotFound() {
        
        UserDto dto = new UserDto();
        when(userRepository.findById(1L)).thenReturn(Optional.empty());


        assertThatThrownBy(() -> userService.update(1L, dto))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void delete_ShouldCallDelete_WhenExists() {
        when(userRepository.existsById(1L)).thenReturn(true);

        userService.delete(1L);

        verify(userRepository).deleteById(1L);
    }

    @Test
    void delete_ShouldThrow_WhenNotExists() {
        when(userRepository.existsById(1L)).thenReturn(false);

        assertThatThrownBy(() -> userService.delete(1L))
                .isInstanceOf(EntityNotFoundException.class);

        verify(userRepository, never()).deleteById(any());
    }
}