package ferym.project.mapper;

import ferym.project.dto.UserDto;
import ferym.project.model.CloudOrder;
import ferym.project.model.CloudUser;
import org.springframework.stereotype.Component;
import java.util.stream.Collectors;

@Component
public class UserMapper {
    public UserDto toDto(CloudUser entity) {
        if (entity == null) {
            return null;
        }
        UserDto dto = new UserDto();
        dto.setId(entity.getId());
        dto.setUsername(entity.getUsername());
        if (entity.getOrders() != null) {
            dto.setOrderIds(entity.getOrders().stream()
                    .map(CloudOrder::getId)
                    .collect(Collectors.toList()));
        }
        return dto;
    }

    public CloudUser toEntity(UserDto dto) {
        if (dto == null) {
            return null;
        }
        CloudUser entity = new CloudUser();
        entity.setId(dto.getId());
        entity.setUsername(dto.getUsername());
        return entity;
    }
}