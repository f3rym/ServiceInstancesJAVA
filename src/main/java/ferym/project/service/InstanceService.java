package ferym.project.service;

import ferym.project.dto.InstanceDto;
import ferym.project.mapper.InstanceMapper;
import ferym.project.repository.InstanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InstanceService {
    private final InstanceRepository repository;
    private final InstanceMapper mapper;


    public InstanceDto getById(Long id) {
        return repository.findById(id)
                .map(mapper::toDto)
                .orElseThrow(() -> new RuntimeException("Инстанс не найден"));
    }

    public List<InstanceDto> getFiltered(String type) {
        if (type == null) return repository.findAll()
                .stream()
                .map(mapper::toDto)
                .toList();

        return repository.findAll().stream()
                .filter(i -> i.getInstanceType().equalsIgnoreCase(type))
                .map(mapper::toDto)
                .toList();
    }
}