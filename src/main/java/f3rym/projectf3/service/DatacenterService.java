package f3rym.projectf3.service;


import f3rym.projectf3.dto.DatacenterDto;
import f3rym.projectf3.mapper.DatacenterMapper;
import f3rym.projectf3.repository.DatacenterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class DatacenterService {
    private final DatacenterRepository repository;
    private final DatacenterMapper mapper;

    public DatacenterDto getById(Long id) {
        return repository.findById(id)
                .map(mapper::toDto)
                .orElseThrow(() -> new RuntimeException("! Unknown datacenter"));
    }
    public List<DatacenterDto> getFiltered(String location) {
        if (location == null)
            return repository.findAll()
                    .stream()
                    .map(mapper::toDto)
                    .toList();
        return repository.findAll().stream()
                .filter(i -> i.getLocation().equalsIgnoreCase(location))
                .map(mapper::toDto)
                .toList();
    }
}

