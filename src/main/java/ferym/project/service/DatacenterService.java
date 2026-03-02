package ferym.project.service;

import ferym.project.dto.DatacenterDto;
import ferym.project.exception.InstanceNotFoundException;
import ferym.project.mapper.DatacenterMapper;
import ferym.project.model.Datacenter;
import ferym.project.repository.DatacenterRepository;
import jakarta.transaction.Transactional;
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
        if (location == null) {
            return repository.findAll()
                    .stream()
                    .map(mapper::toDto)
                    .toList();
        }
        return repository.findAll().stream()
                .filter(i -> i.getLocation().equalsIgnoreCase(location))
                .map(mapper::toDto)
                .toList();
    }
    @Transactional
    public DatacenterDto create(DatacenterDto dto) {
        Datacenter entity = new Datacenter();
        entity.setName(dto.getName());
        entity.setLocation(dto.getLocation());
        return mapper.toDto(repository.save(entity));
    }

    @Transactional
    public void delete(Long id) {
        repository.deleteById(id);
    }
    @Transactional
    public DatacenterDto update(Long id, DatacenterDto dto) {
        Datacenter entity = repository.findById(id)
                .orElseThrow(() -> new InstanceNotFoundException("ПО не найдено с id: " + id));
        entity.setName(dto.getName());
        entity.setLocation(dto.getLocation());
        return mapper.toDto(entity);
    }
}
