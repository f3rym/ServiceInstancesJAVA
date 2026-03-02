package ferym.project.service;

import ferym.project.dto.UserDto;
import ferym.project.exception.InstanceNotFoundException;
import ferym.project.mapper.UserMapper;
import ferym.project.model.CloudUser;
import ferym.project.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper mapper;

    public List<UserDto> getAll() {
        return userRepository.findAll().stream()
                .map(mapper::toDto)
                .toList();
    }
    public UserDto getById(Long id) {
        return userRepository.findById(id)
                .map(mapper::toDto)
                .orElseThrow(() -> new InstanceNotFoundException("Пользователь не найден с id: " + id));
    }
    @Transactional
    public UserDto updateUsername(Long id, String username) {
        CloudUser user = userRepository.findById(id)
                .orElseThrow(() -> new InstanceNotFoundException("Пользователь не найден с id: " + id));
        user.setUsername(username);
        return mapper.toDto(user);
    }
    @Transactional
    public UserDto create(UserDto dto) {
        CloudUser instance = mapper.toEntity(dto);
        CloudUser savedInstance = userRepository.save(instance);
        return mapper.toDto(savedInstance);
    }


    @Transactional
    public void delete(Long id) {
        userRepository.deleteById(id);
    }



}
