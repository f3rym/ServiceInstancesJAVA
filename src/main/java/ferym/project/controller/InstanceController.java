package ferym.project.controller;

import ferym.project.dto.InstanceDto;
import ferym.project.dto.InstanceFilterDto;
import ferym.project.service.InstanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/v1/instances")
@RequiredArgsConstructor
public class InstanceController {
    private final InstanceService service;

    @GetMapping("/{id}")
    public InstanceDto getInstanceById(@PathVariable Long id) {

        return service.getById(id);
    }

    @GetMapping
    public List<InstanceDto> getInstances(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String status
    ) {
        InstanceFilterDto filter = new InstanceFilterDto(type, status);
        return service.getFiltered(filter);
    }
}