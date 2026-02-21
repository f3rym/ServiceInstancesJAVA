package ferym.project.controller;


import ferym.project.dto.DatacenterDto;
import ferym.project.service.DatacenterService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/datacenters")
@RequiredArgsConstructor
public class DatacenterController {
    private final DatacenterService service;

    @GetMapping("/{id}")
    public DatacenterDto getDatacenterId(@PathVariable Long id) {
        return service.getById(id);
    }
    @GetMapping
    public List<DatacenterDto> getDatacenters(@RequestParam(required = false) String location) {
        return service.getFiltered(location);
    }
}
