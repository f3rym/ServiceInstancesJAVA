package ferym.project.service;

import ferym.project.dto.UserDto;
import ferym.project.mapper.UserMapper;
import ferym.project.model.CloudUser;
import ferym.project.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    // Чтение всех пользователей (решение проблемы N+1 уже заложено в репозитории через EntityGraph)
    @Transactional(readOnly = true)
    public List<UserDto> getAll() {
        return userRepository.findAll().stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
    }

    // Чтение одного пользователя по ID
    @Transactional(readOnly = true)
    public UserDto getById(Long id) {
        return userRepository.findById(id)
                .map(userMapper::toDto)
                .orElseThrow(() -> new RuntimeException("Пользователь с id " + id + " не найден"));
    }

    // Создание пользователя
    @Transactional
    public UserDto create(UserDto dto) {
        CloudUser entity = userMapper.toEntity(dto);
        CloudUser savedEntity = userRepository.save(entity);
        return userMapper.toDto(savedEntity);
    }

    // Обновление пользователя (полное или только имя)
    @Transactional
    public UserDto update(Long id, UserDto dto) {
        CloudUser entity = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        // Обновляем только нужные поля
        entity.setUsername(dto.getUsername());

        // В JPA/Hibernate вызывать save() при обновлении в транзакции не обязательно (Dirty Checking),
        // но для наглядности кода можно оставить.
        return userMapper.toDto(entity);
    }

    // Удаление пользователя
    // Благодаря CascadeType.ALL в сущности CloudUser, удаление юзера удалит и все его заказы
    @Transactional
    public void delete(Long id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("Невозможно удалить: пользователь не найден");
        }
        userRepository.deleteById(id);
    }
}