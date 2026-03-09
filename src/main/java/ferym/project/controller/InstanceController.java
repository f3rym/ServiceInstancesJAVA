package ferym.project.controller;

import ferym.project.dto.InstanceDto;
import ferym.project.dto.InstanceFilterDto;
import ferym.project.service.InstanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import org.springframework.data.domain.Pageable;
import java.util.List;

@RestController
@RequestMapping("/api/v1/instances")
@RequiredArgsConstructor
public class InstanceController {

    private final InstanceService instanceService;

    @GetMapping
    public List<InstanceDto> getAll() {
        return instanceService.getAll();
    }

    @GetMapping("/{id}")
    public InstanceDto getById(@PathVariable Long id) {
        return instanceService.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public InstanceDto create(@RequestBody InstanceDto dto) {
        return instanceService.create(dto);
    }

    @PutMapping("/{id}")
    public InstanceDto update(@PathVariable Long id, @RequestBody InstanceDto dto) {
        return instanceService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        instanceService.delete(id);
    }

    @GetMapping("/search")
    public Page<InstanceDto> search(
            InstanceFilterDto filter,
            Pageable pageable) {
        return instanceService.search(filter, pageable);
    }
}