package ferym.project.service;

import ferym.project.dto.InstanceDto;
import ferym.project.dto.InstanceFilterDto;
import ferym.project.exception.InstanceNotFoundException;
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
                .orElseThrow(() -> new InstanceNotFoundException("Инстанс не найден с id: " + id));
    }

    public List<InstanceDto> getFiltered(InstanceFilterDto filter) {
        return repository.findAll().stream()
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
