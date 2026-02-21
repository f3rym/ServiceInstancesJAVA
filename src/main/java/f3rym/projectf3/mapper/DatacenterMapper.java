package f3rym.projectf3.mapper;


import f3rym.projectf3.dto.DatacenterDto;
import f3rym.projectf3.model.Datacenter;
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
