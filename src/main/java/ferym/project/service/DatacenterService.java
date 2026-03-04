package ferym.project.service;

import ferym.project.dto.DatacenterDto;
import ferym.project.mapper.DatacenterMapper;
import ferym.project.model.Datacenter;
import ferym.project.repository.DatacenterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DatacenterService {
    private final DatacenterRepository datacenterRepository;
    private final DatacenterMapper mapper;

    @Transactional(readOnly = true)
    public List<DatacenterDto> getAll() {
        return datacenterRepository.findAll().stream().map(mapper::toDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public DatacenterDto getById(Long id) {
        return datacenterRepository.findById(id).map(mapper::toDto)
                .orElseThrow(() -> new RuntimeException("Дата-центр не найден"));
    }

    @Transactional
    public DatacenterDto create(DatacenterDto dto) {
        Datacenter entity = mapper.toEntity(dto);
        return mapper.toDto(datacenterRepository.save(entity));
    }

    @Transactional
    public DatacenterDto update(Long id, DatacenterDto dto) {
        Datacenter entity = datacenterRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Дата-центр не найден"));
        entity.setName(dto.getName());
        entity.setLocation(dto.getLocation());
        return mapper.toDto(entity);
    }

    @Transactional
    public void delete(Long id) {
        datacenterRepository.deleteById(id);
    }
}