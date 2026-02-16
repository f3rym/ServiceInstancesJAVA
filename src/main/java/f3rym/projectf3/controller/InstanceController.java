package f3rym.projectf3.controller;

import f3rym.projectf3.dto.InstanceDto;
import f3rym.projectf3.mapper.InstanceMapper;
import f3rym.projectf3.service.InstanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/instances")
@RequiredArgsConstructor
public class InstanceController {
    private final InstanceService service;

    @GetMapping("/{id}")
    public InstanceDto getInstanceById(@PathVariable Long id) {
        return service.getById(id);
    }

    @GetMapping
    public List<InstanceDto> getInstances(@RequestParam(required = false) String type) {
        return service.getFiltered(type);
    }
}