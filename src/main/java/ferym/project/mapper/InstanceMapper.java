package ferym.project.mapper;

import ferym.project.dto.InstanceDto;
import ferym.project.model.CloudInstance;
import org.springframework.stereotype.Component;

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
        dto.setStatus(entity.getStatus());
        dto.setOs(entity.getOs());
        dto.setPrice(entity.getPrice());
        if (entity.getDatacenter() != null) {
            dto.setDatacenter(entity.getDatacenter().getName());
        } else {
            dto.setDatacenter(null);
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
        entity.setStatus(dto.getStatus());
        entity.setOs(dto.getOs());
        entity.setPrice(dto.getPrice());
        return entity;
    }

    public void updateEntity(InstanceDto dto, CloudInstance entity) {
        if (dto == null || entity == null) {
            return;
        }
        entity.setName(dto.getName());
        entity.setInstanceType(dto.getInstanceType());
        entity.setStatus(dto.getStatus());
        entity.setOs(dto.getOs());
        entity.setPrice(dto.getPrice());
    }
}

