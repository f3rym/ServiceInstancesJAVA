package ferym.project.mapper;


import ferym.project.dto.DatacenterDto;
import ferym.project.model.Datacenter;
import org.springframework.stereotype.Component;

@Component
public class DatacenterMapper {
    public DatacenterDto toDto(Datacenter entity) {
        DatacenterDto dto = new DatacenterDto();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setLocation(entity.getLocation());
        return dto;
    }
}
