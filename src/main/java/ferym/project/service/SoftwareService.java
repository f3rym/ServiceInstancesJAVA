package ferym.project.service;

import ferym.project.dto.SoftwareDto;
import ferym.project.mapper.SoftwareMapper;
import ferym.project.model.Software;
import ferym.project.repository.SoftwareRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SoftwareService {
    private final SoftwareRepository softwareRepository;
    private final SoftwareMapper mapper;

    @Transactional(readOnly = true)
    public List<SoftwareDto> getAll() {
        return softwareRepository.findAll().stream().map(mapper::toDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public SoftwareDto getById(Long id) {
        return softwareRepository.findById(id).map(mapper::toDto)
                .orElseThrow(() -> new RuntimeException("ПО не найдено"));
    }

    @Transactional
    public SoftwareDto create(SoftwareDto dto) {
        Software entity = mapper.toEntity(dto);
        return mapper.toDto(softwareRepository.save(entity));
    }

    @Transactional
    public SoftwareDto update(Long id, SoftwareDto dto) {
        Software entity = softwareRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("ПО не найдено"));
        entity.setName(dto.getName());
        entity.setVersion(dto.getVersion());
        return mapper.toDto(entity);
    }

    @Transactional
    public void delete(Long id) {
        softwareRepository.deleteById(id);
    }
}