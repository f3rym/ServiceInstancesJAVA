package ferym.project.controller;

import ferym.project.dto.DatacenterDto;
import ferym.project.service.DatacenterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/datacenters")
@RequiredArgsConstructor
public class DatacenterController {

    private final DatacenterService datacenterService;

    @GetMapping
    public List<DatacenterDto> getAll() {
        return datacenterService.getAll();
    }

    @GetMapping("/{id}")
    public DatacenterDto getById(@PathVariable Long id) {
        return datacenterService.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DatacenterDto create(@RequestBody DatacenterDto dto) {
        return datacenterService.create(dto);
    }

    @PutMapping("/{id}")
    public DatacenterDto update(@PathVariable Long id, @RequestBody DatacenterDto dto) {
        return datacenterService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        datacenterService.delete(id);
    }
}