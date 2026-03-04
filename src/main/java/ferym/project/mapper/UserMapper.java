package ferym.project.mapper;

import ferym.project.dto.UserDto;
import ferym.project.model.Order;
import ferym.project.model.User;
import org.springframework.stereotype.Component;
import java.util.stream.Collectors;

@Component
public class UserMapper {
    public UserDto toDto(User entity) {
        if (entity == null) {
            return null;
        }
        UserDto dto = new UserDto();
        dto.setId(entity.getId());
        dto.setUsername(entity.getUsername());
        if (entity.getOrders() != null) {
            dto.setOrderIds(entity.getOrders().stream()
                    .map(Order::getId)
                    .collect(Collectors.toList()));
        }
        return dto;
    }

    public User toEntity(UserDto dto) {
        if (dto == null) {
            return null;
        }
        User entity = new User();
        entity.setId(dto.getId());
        entity.setUsername(dto.getUsername());
        return entity;
    }
}