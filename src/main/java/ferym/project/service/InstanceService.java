package ferym.project.service;

import ferym.project.dto.InstanceDto;
import ferym.project.dto.InstanceFilterDto;
import ferym.project.exception.InstanceNotFoundException;
import ferym.project.mapper.InstanceMapper;
import ferym.project.model.CloudInstance;
import ferym.project.model.Datacenter;
import ferym.project.repository.DatacenterRepository;
import ferym.project.repository.InstanceRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InstanceService {

    private final InstanceRepository instanceRepository;
    private final DatacenterRepository datacenterRepository; // Нужно для связи
    private final InstanceMapper mapper;

    public List<InstanceDto> getAll() {
        return instanceRepository.findAll().stream()
                .map(mapper::toDto)
                .toList();
    }

    public InstanceDto getById(Long id) {
        return instanceRepository.findById(id)
                .map(mapper::toDto)
                .orElseThrow(() -> new InstanceNotFoundException("Инстанс не найден с id: " + id));
    }

    @Transactional
    public InstanceDto update(Long id, InstanceDto dto) {
        CloudInstance instance = instanceRepository.findById(id)
                .orElseThrow(() -> new InstanceNotFoundException("Инстанс не найден с id: " + id));

        // 1. Обновляем простые поля (имя, тип, ос и т.д.)
        mapper.updateEntity(dto, instance);

        // 2. Обновляем связь с датацентром, если имя передано
        if (dto.getDatacenter() != null) {
            Datacenter dc = datacenterRepository.findByName(dto.getDatacenter())
                    .orElseThrow(() -> new RuntimeException("Датацентр не найден: " + dto.getDatacenter()));
            instance.setDatacenter(dc);
        }

        return mapper.toDto(instanceRepository.save(instance));
    }

    @Transactional
    public InstanceDto updateStatus(Long id, String status) {
        CloudInstance instance = instanceRepository.findById(id)
                .orElseThrow(() -> new InstanceNotFoundException("Инстанс не найден с id: " + id));
        instance.setStatus(status);
        return mapper.toDto(instance);
    }

    @Transactional
    public InstanceDto create(InstanceDto dto) {
        CloudInstance instance = mapper.toEntity(dto);

        // При создании тоже нужно привязать существующий датацентр
        if (dto.getDatacenter() != null) {
            Datacenter dc = datacenterRepository.findByName(dto.getDatacenter())
                    .orElseThrow(() -> new RuntimeException("Датацентр не найден: " + dto.getDatacenter()));
            instance.setDatacenter(dc);
        }

        CloudInstance savedInstance = instanceRepository.save(instance);
        return mapper.toDto(savedInstance);
    }

    @Transactional
    public void delete(Long id) {
        instanceRepository.deleteById(id);
    }

    public List<InstanceDto> getFiltered(InstanceFilterDto filter) {
        return instanceRepository.findAll().stream()
                .filter(i -> {
                    if (filter == null) {
                        return true;
                    }
                    boolean matchType = filter.type() == null
                            || i.getInstanceType().equalsIgnoreCase(filter.type());
                    boolean matchStatus = filter.status() == null
                            || i.getStatus().equalsIgnoreCase(filter.status());
                    return matchType && matchStatus;
                })
                .map(mapper::toDto)
                .toList();
    }
}