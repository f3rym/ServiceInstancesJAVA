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
    public List<DatacenterDto> getDatacenters(@RequestParam(required = false) String location) {
        return datacenterService.getFiltered(location);
    }

    @GetMapping("/{id}")
    public DatacenterDto getDatacenterById(@PathVariable Long id) {
        return datacenterService.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DatacenterDto createDatacenter(@RequestBody DatacenterDto dto) {
        return datacenterService.create(dto);
    }

    @PutMapping("/{id}")
    public DatacenterDto updateDatacenter(@PathVariable Long id, @RequestBody DatacenterDto dto) {
        return datacenterService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteDatacenter(@PathVariable Long id) {
        datacenterService.delete(id);
    }
}