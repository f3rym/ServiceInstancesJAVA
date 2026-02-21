package f3rym.projectf3.controller;


import f3rym.projectf3.dto.DatacenterDto;
import f3rym.projectf3.service.DatacenterService;
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
