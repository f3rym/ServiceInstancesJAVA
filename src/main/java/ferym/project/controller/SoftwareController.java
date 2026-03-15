package ferym.project.controller;

import ferym.project.dto.SoftwareDto;
import ferym.project.service.SoftwareService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/software")
@RequiredArgsConstructor
public class SoftwareController {

    private final SoftwareService softwareService;

    @GetMapping
    public List<SoftwareDto> getAll() {
        return softwareService.getAll();
    }

    @GetMapping("/{id}")
    public SoftwareDto getById(@PathVariable Long id) {
        return softwareService.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SoftwareDto create(@Valid @RequestBody SoftwareDto dto) {
        return softwareService.create(dto);
    }

    @PutMapping("/{id}")
    public SoftwareDto update(@PathVariable Long id, @RequestBody SoftwareDto dto) {
        return softwareService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        softwareService.delete(id);
    }
}