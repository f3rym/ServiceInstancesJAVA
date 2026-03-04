package ferym.project.mapper;

import ferym.project.dto.DatacenterDto;
import ferym.project.model.Datacenter;
import org.springframework.stereotype.Component;

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