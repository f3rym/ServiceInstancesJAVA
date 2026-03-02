package ferym.project.mapper;

import ferym.project.dto.UserDto;
import ferym.project.model.CloudUser;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public UserDto toDto(CloudUser entity) {
        if (entity == null) {
            return null;
        }
        UserDto dto = new UserDto();
        dto.setId(entity.getId());
        dto.setUsername(entity.getUsername());
        dto.setOrders(entity.getOrders());
        return dto;
    }
    public CloudUser toEntity(UserDto dto) {
        if (dto == null) {
            return null;
        }
        CloudUser entity = new CloudUser();
        entity.setUsername(dto.getUsername());
        entity.setOrders(dto.getOrders());
        return entity;
    }
}
