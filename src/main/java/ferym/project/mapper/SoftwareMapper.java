package ferym.project.mapper;

import ferym.project.dto.SoftwareDto;
import ferym.project.model.Software;
import org.springframework.stereotype.Component;

@Component
public class SoftwareMapper {
    public SoftwareDto toDto(Software entity) {
        if (entity == null) {
            return null;
        }

        SoftwareDto dto = new SoftwareDto();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setVersion(entity.getVersion());
        return dto;
    }

    public Software toEntity(SoftwareDto dto) {
        if (dto == null) {
            return null;
        }

        Software entity = new Software();
        entity.setId(dto.getId());
        entity.setName(dto.getName());
        entity.setVersion(dto.getVersion());
        return entity;
    }
}