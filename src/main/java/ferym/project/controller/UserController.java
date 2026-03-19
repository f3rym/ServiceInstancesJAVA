package ferym.project.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import ferym.project.dto.UserDto;
import ferym.project.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "Пользователи", description = "Управление учетными записями клиентов")
public class UserController {

    private final UserService userService;

    @GetMapping
    @Operation(summary = "Список всех пользователей")
    public List<UserDto> getAll() {
        return userService.getAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Информация о пользователе по ID")
    public UserDto getById(@PathVariable Long id) {
        return userService.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Регистрация нового пользователя")
    public UserDto create(@Valid @RequestBody UserDto dto) {
        return userService.create(dto);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Изменить данные пользователя")
    public UserDto update(@PathVariable Long id, @RequestBody UserDto dto) {
        return userService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Удалить пользователя")
    public void delete(@PathVariable Long id) {
        userService.delete(id);
    }
}