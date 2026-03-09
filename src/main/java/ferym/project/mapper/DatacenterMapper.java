package ferym.project.mapper;

import ferym.project.dto.DatacenterDto;
import ferym.project.model.Datacenter;
import ferym.project.model.Instance;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class DatacenterMapper {
    public DatacenterDto toDto(Datacenter entity) {
        if (entity == null) {
            return null;
        }

        DatacenterDto dto = new DatacenterDto();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setLocation(entity.getLocation());

        if (entity.getInstances() != null) {
            dto.setInstanceIds(entity.getInstances().stream()
                    .map(Instance::getId)
                    .collect(Collectors.toSet()));
        }

        return dto;
    }

    public Datacenter toEntity(DatacenterDto dto) {
        if (dto == null) {
            return null;
        }

        Datacenter entity = new Datacenter();
        entity.setId(dto.getId());
        entity.setName(dto.getName());
        entity.setLocation(dto.getLocation());

        return entity;
    }
}