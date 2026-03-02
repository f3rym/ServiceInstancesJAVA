package ferym.project.service;

import ferym.project.dto.SoftwareDto;
import ferym.project.exception.InstanceNotFoundException;
import ferym.project.mapper.SoftwareMapper;
import ferym.project.model.Software;
import ferym.project.repository.SoftwareRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SoftwareService {
    private final SoftwareRepository repository;
    private final SoftwareMapper mapper;

    public List<SoftwareDto> getAll() {
        return repository.findAll().stream()
                .map(mapper::toDto)
                .toList();
    }

    public SoftwareDto getById(Long id) {
        return repository.findById(id)
                .map(mapper::toDto)
                .orElseThrow(() -> new InstanceNotFoundException("ПО не найдено с id: " + id));
    }

    @Transactional
    public SoftwareDto create(SoftwareDto dto) {
        Software entity = mapper.toEntity(dto);
        return mapper.toDto(repository.save(entity));
    }

    @Transactional
    public SoftwareDto update(Long id, SoftwareDto dto) {
        Software entity = repository.findById(id)
                .orElseThrow(() -> new InstanceNotFoundException("ПО не найдено с id: " + id));
        entity.setName(dto.getName());
        entity.setVersion(dto.getVersion());
        return mapper.toDto(entity);
    }

    @Transactional
    public void delete(Long id) {
        repository.deleteById(id);
    }
}