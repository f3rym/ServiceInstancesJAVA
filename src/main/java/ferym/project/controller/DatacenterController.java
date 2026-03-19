package ferym.project.controller;

import ferym.project.dto.DatacenterDto;
import ferym.project.service.DatacenterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/datacenters")
@RequiredArgsConstructor
@Tag(name = "Дата-центры", description = "Управление локациями и ресурсами ЦОД")
public class DatacenterController {

    private final DatacenterService datacenterService;

    @GetMapping
    @Operation(summary = "Получить список всех дата-центров")
    public List<DatacenterDto> getAll() {
        return datacenterService.getAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить дата-центр по ID")
    public DatacenterDto getById(@PathVariable Long id) {
        return datacenterService.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Создать новый дата-центр")
    public DatacenterDto create(@Valid @RequestBody DatacenterDto dto) {
        return datacenterService.create(dto);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Обновить данные дата-центра")
    public DatacenterDto update(@PathVariable Long id, @RequestBody DatacenterDto dto) {
        return datacenterService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Удалить дата-центр")
    public void delete(@PathVariable Long id) {
        datacenterService.delete(id);
    }
}