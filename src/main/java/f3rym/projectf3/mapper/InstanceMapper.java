package f3rym.projectf3.mapper;

import f3rym.projectf3.dto.InstanceDto;
import f3rym.projectf3.model.CloudInstance;
import org.springframework.stereotype.Component;

@Component
public class InstanceMapper {
    public InstanceDto toDto(CloudInstance entity) {
        InstanceDto dto = new InstanceDto();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setInstanceType(entity.getInstanceType());
        dto.setStatus(entity.getStatus());
        return dto;
    }
}
// map struct

