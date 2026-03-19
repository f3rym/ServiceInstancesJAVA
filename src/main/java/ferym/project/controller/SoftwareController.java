package ferym.project.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import ferym.project.dto.SoftwareDto;
import ferym.project.service.SoftwareService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/software")
@RequiredArgsConstructor
@Tag(name = "Программное обеспечение", description = "Управление списком доступного ПО для инстансов")
public class SoftwareController {

    private final SoftwareService softwareService;

    @GetMapping
    @Operation(summary = "Получить весь список ПО")
    public List<SoftwareDto> getAll() {
        return softwareService.getAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Найти ПО по ID")
    public SoftwareDto getById(@PathVariable Long id) {
        return softwareService.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Добавить новое ПО")
    public SoftwareDto create(@Valid @RequestBody SoftwareDto dto) {
        return softwareService.create(dto);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Обновить данные ПО")
    public SoftwareDto update(@PathVariable Long id, @RequestBody SoftwareDto dto) {
        return softwareService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Удалить ПО из системы")
    public void delete(@PathVariable Long id) {
        softwareService.delete(id);
    }
}