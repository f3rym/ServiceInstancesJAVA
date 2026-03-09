package ferym.project.mapper;

import ferym.project.dto.InstanceDto;
import ferym.project.model.Datacenter;
import ferym.project.model.Instance;
import ferym.project.model.Software;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class InstanceMapper {
    public InstanceDto toDto(Instance entity) {
        if (entity == null) {
            return null;
        }
        InstanceDto dto = new InstanceDto();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setInstanceType(entity.getInstanceType());
        dto.setOs(entity.getOs());
        dto.setPrice(entity.getPrice());

        if (entity.getDatacenters() != null) {
            dto.setDatacenterIds(entity.getDatacenters().stream()
                    .map(Datacenter::getId)
                    .collect(Collectors.toSet()));
        }

        if (entity.getInstalledSoftware() != null) {
            dto.setSoftwareIds(entity.getInstalledSoftware().stream()
                    .map(Software::getId)
                    .collect(Collectors.toList()));
        }
        return dto;
    }

    public Instance toEntity(InstanceDto dto) {
        if (dto == null) {
            return null;
        }
        Instance entity = new Instance();
        if (dto.getId() != null) {
            entity.setId(dto.getId());
        }
        entity.setName(dto.getName());
        entity.setInstanceType(dto.getInstanceType());
        entity.setOs(dto.getOs());
        entity.setPrice(dto.getPrice());

        return entity;
    }
}