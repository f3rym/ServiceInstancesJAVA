package ferym.project.service;

import ferym.project.dto.InstanceDto;
import ferym.project.mapper.InstanceMapper;
import ferym.project.model.Instance;
import ferym.project.model.Datacenter;
import ferym.project.model.Software;
import ferym.project.repository.DatacenterRepository;
import ferym.project.repository.InstanceRepository;
import ferym.project.repository.SoftwareRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InstanceService {
    private final InstanceRepository instanceRepository;
    private final DatacenterRepository datacenterRepository;
    private final SoftwareRepository softwareRepository;
    private final InstanceMapper mapper;

    @Transactional(readOnly = true)
    public List<InstanceDto> getAll() {
        return instanceRepository.findAll().stream()
                .map(mapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public InstanceDto getById(Long id) {
        return instanceRepository.findById(id).map(mapper::toDto)
                .orElseThrow(() -> new RuntimeException("Инстанс не найден"));
    }

    @Transactional
    public InstanceDto create(InstanceDto dto) {
        Instance entity = mapper.toEntity(dto);

        if (dto.getDatacenterName() != null) {
            Datacenter dc = datacenterRepository.findByName(dto.getDatacenterName())
                    .orElseThrow(() -> new RuntimeException("Дата-центр не найден"));
            entity.setDatacenter(dc);
        }

        if (dto.getSoftwareIds() != null && !dto.getSoftwareIds().isEmpty()) {
            List<Software> softList = softwareRepository.findAllById(dto.getSoftwareIds());
            entity.setInstalledSoftware(new HashSet<>(softList));
        }

        return mapper.toDto(instanceRepository.save(entity));
    }

    @Transactional
    public InstanceDto update(Long id, InstanceDto dto) {
        Instance entity = instanceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Инстанс не найден"));

        entity.setName(dto.getName());
        entity.setStatus(dto.getStatus());
        entity.setPrice(dto.getPrice());

        return mapper.toDto(entity);
    }

    @Transactional
    public void delete(Long id) {
        instanceRepository.deleteById(id);
    }
}