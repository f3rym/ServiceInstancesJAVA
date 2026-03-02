package ferym.project.controller;

import ferym.project.dto.InstanceDto;
import ferym.project.dto.InstanceFilterDto;
import ferym.project.service.InstanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/instances")
@RequiredArgsConstructor
public class InstanceController {

    private final InstanceService service;

    @GetMapping
    public List<InstanceDto> getInstances(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String status
    ) {
        InstanceFilterDto filter = new InstanceFilterDto(type, status);
        return service.getFiltered(filter);
    }

    @GetMapping("/{id}")
    public InstanceDto getInstanceById(@PathVariable Long id) {
        return service.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public InstanceDto createInstance(@RequestBody InstanceDto dto) {
        return service.create(dto);
    }

    @PutMapping("/{id}")
    public InstanceDto updateInstance(@PathVariable Long id, @RequestBody InstanceDto dto) {
        return service.update(id, dto);
    }

    @PatchMapping("/{id}/status")
    public InstanceDto updateStatus(@PathVariable Long id, @RequestParam String status) {
        return service.updateStatus(id, status);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteInstance(@PathVariable Long id) {
        service.delete(id);
    }
}