package ferym.project.service;

import ferym.project.dto.UserDto;
import ferym.project.mapper.UserMapper;
import ferym.project.model.User;
import ferym.project.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Transactional(readOnly = true)
    public List<UserDto> getAll() {
        return userRepository.findAll().stream()
                .map(userMapper::toDto)
                .toList();
    }

    // Чтение одного пользователя по ID
    @Transactional(readOnly = true)
    public UserDto getById(Long id) {
        return userRepository.findById(id)
                .map(userMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь с id " + id + " не найден"));
    }

    // Создание пользователя
    @Transactional
    public UserDto create(UserDto dto) {
        User entity = userMapper.toEntity(dto);
        User savedEntity = userRepository.save(entity);
        return userMapper.toDto(savedEntity);
    }

    @Transactional
    public UserDto update(Long id, UserDto dto) {
        User entity = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден"));

        entity.setUsername(dto.getUsername());

        return userMapper.toDto(entity);
    }

    @Transactional
    public void delete(Long id) {
        if (!userRepository.existsById(id)) {
            throw new EntityNotFoundException("Невозможно удалить: пользователь не найден");
        }
        userRepository.deleteById(id);
    }
}