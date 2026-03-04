package ferym.project.mapper;

import ferym.project.dto.InstanceDto;
import ferym.project.model.CloudInstance;
import ferym.project.model.Software;
import org.springframework.stereotype.Component;
import java.util.stream.Collectors;

@Component
public class InstanceMapper {
    public InstanceDto toDto(CloudInstance entity) {
        if (entity == null) {
            return null;
        }
        InstanceDto dto = new InstanceDto();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setInstanceType(entity.getInstanceType());
        dto.setOs(entity.getOs());
        dto.setPrice(entity.getPrice());
        dto.setStatus(entity.getStatus());
        if (entity.getDatacenter() != null) {
            dto.setDatacenterName(entity.getDatacenter().getName());
        }
        if (entity.getInstalledSoftware() != null) {
            dto.setSoftwareIds(entity.getInstalledSoftware().stream()
                    .map(Software::getId)
                    .collect(Collectors.toList()));
        }
        return dto;
    }

    public CloudInstance toEntity(InstanceDto dto) {
        if (dto == null) {
            return null;
        }
        CloudInstance entity = new CloudInstance();
        entity.setName(dto.getName());
        entity.setInstanceType(dto.getInstanceType());
        entity.setOs(dto.getOs());
        entity.setPrice(dto.getPrice());
        entity.setStatus(dto.getStatus());
        return entity;
    }
}