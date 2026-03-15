package ferym.project.controller;

import ferym.project.dto.InstanceDto;
import ferym.project.dto.InstanceFilterDto;
import ferym.project.service.InstanceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import org.springframework.data.domain.Pageable;
import java.util.List;

@RestController
@RequestMapping("/api/v1/instances")
@RequiredArgsConstructor
@Tag(name = "Инстансы", description = "Управление облачными серверами") // Пункт 6 (Swagger)
public class InstanceController {

    private final InstanceService instanceService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Создать инстанс", description = "Создает новый сервер и инвалидирует кэш")
    public InstanceDto create(@Valid @RequestBody InstanceDto dto) {
        return instanceService.create(dto);
    }

    @GetMapping
    @Operation(summary = "Получить все", description = "Возвращает полный список инстансов")
    public List<InstanceDto> getAll() {
        return instanceService.getAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить по ID")
    public InstanceDto getById(@PathVariable Long id) {
        return instanceService.getById(id);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Обновить инстанс")
    public InstanceDto update(@PathVariable Long id, @Valid @RequestBody InstanceDto dto) {
        return instanceService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Удалить инстанс")
    public void delete(@PathVariable Long id) {
        instanceService.delete(id);
    }

    @GetMapping("/search")
    @Operation(summary = "Поиск с фильтрацией и пагинацией", description = "Пункт 3 (Pageable)")
    public Page<InstanceDto> search(
            InstanceFilterDto filter,
            Pageable pageable) {
        return instanceService.search(filter, pageable);
    }
}